package world.shanya.serialport.service

import android.app.IntentService
import android.content.Intent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.connect.SerialPortConnect
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.SerialPortTools

/**
 * SerialPortService 接收数据服务
 * @UpdateContent
 * 1. 修复中文乱码问题
 * @Author Shanya
 * @Date 2021-12-10
 * @Version 4.1.2
 */
class SerialPortService : IntentService("SerialPortService") {

    override fun onCreate() {
        super.onCreate()
        LogUtil.log("传统蓝牙收消息服务开启")
    }

    override fun onHandleIntent(intent: Intent?) {
        var len: Int
        var receivedData: String
        var buffer = ByteArray(0)
        var flag = false

        while (SerialPortConnect.connectStatus) {
            Thread.sleep(100)
            if (SerialPortConnect.connectStatus){
                len = SerialPortConnect.inputStream?.available()!!
                while (len != 0) {
                    flag = true
                    buffer = ByteArray(len)
                    SerialPortConnect.inputStream?.read(buffer)
                    Thread.sleep(10)
                    len = SerialPortConnect.inputStream?.available()!!
                }
            }
            if (flag) {
                receivedData = if (SerialPort.readDataType == SerialPort.READ_STRING) {
                    SerialPortTools.bytes2string(buffer, "GBK")
                } else {
                    val sb = StringBuilder()
                    for (i in buffer) {
                        sb.append("${String.format("%2X", i)} ")
                    }

                    if (SerialPort.hexStringToStringFlag) {
                        SerialPort._hexStringToString(sb.toString()).toString()
                    } else {
                        sb.toString()
                    }
                }
                LogUtil.log("传统设备收到数据", receivedData)
                MainScope().launch {
                    SerialPort.receivedDataCallback?.invoke(receivedData)
                    SerialPort.receivedBytesCallback?.invoke(buffer)
                }
                flag = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.log("传统蓝牙收消息服务关闭")
        SerialPortConnect.disconnectResult(this)
    }
}