package world.shanya.serialport.service

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Message
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.connect.SerialPortConnect
import world.shanya.serialport.tools.LogUtil
import java.nio.charset.StandardCharsets

/**
 * SerialPortService 接收数据服务
 * @UpdateContent
 * 1. 优化断开连接相关处理
 * 2. 优化部分日志和提示信息
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
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
                    String(buffer, StandardCharsets.UTF_8)
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