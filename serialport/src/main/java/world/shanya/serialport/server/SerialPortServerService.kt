package world.shanya.serialport.server

import android.app.IntentService
import android.content.Intent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.SerialPortTools

/**
 * SerialPortServerService 服务端接收数据服务
 * @Author Shanya
 * @Date 2022-03-07
 * @Version 4.1.8
 */
class SerialPortServerService : IntentService("SerialPortServerService") {

    override fun onCreate() {
        super.onCreate()
        LogUtil.log("蓝牙服务端收消息服务开启")
    }

    override fun onHandleIntent(intent: Intent?) {
        var len: Int
        var receivedData: String
        var buffer = ByteArray(0)
        var flag = false

        while (SerialPortServer.connectStatus) {
            Thread.sleep(100)
            if (SerialPortServer.connectStatus){
                len = SerialPortServer.serialPortBluetoothServerSocket?.inputStream?.available()!!
                while (len != 0) {
                    flag = true
                    buffer = ByteArray(len)
                    SerialPortServer.serialPortBluetoothServerSocket?.inputStream?.read(buffer)
                    Thread.sleep(10)
                    len = SerialPortServer.serialPortBluetoothServerSocket?.inputStream?.available()!!
                }
            }
            if (flag) {
                receivedData =
                    SerialPortTools.bytes2string(buffer, "GBK")
                LogUtil.log("服务端收到数据", receivedData)
                MainScope().launch {
                    SerialPortServer.serverReceivedDataCallback?.invoke(receivedData)
                }
                flag = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.log("蓝牙服务端收消息服务关闭")
        SerialPortServer._disconnect()
    }
}