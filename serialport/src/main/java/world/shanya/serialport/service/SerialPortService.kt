package world.shanya.serialport.service

import android.app.IntentService
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Intent
import android.os.Bundle
import android.os.Message
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.connect.SerialPortConnect
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.tools.ToastUtil
import java.nio.charset.StandardCharsets

/**
 * SerialPortService 接收数据服务
 * @Author Shanya
 * @Date 2021-3-16
 * @Version 3.0.0
 */
class SerialPortService : IntentService("SerialPortService") {

    override fun onCreate() {
        super.onCreate()
        SerialPort.logUtil.log("SerialPortService","Create")
    }

    override fun onHandleIntent(intent: Intent?) {
        var len: Int
        var receivedData: String
        var buffer = ByteArray(0)
        var flag = false

        while (SerialPort.connectStatus) {
            Thread.sleep(100)
            if (SerialPort.connectStatus){
                len = SerialPort.inputStream?.available()!!
                while (len != 0) {
                    flag = true
                    buffer = ByteArray(len)
                    SerialPort.inputStream?.read(buffer)
                    Thread.sleep(10)
                    len = SerialPort.inputStream?.available()!!
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
                val bundle = Bundle()
                bundle.putString("SerialPortReceivedData", receivedData)
                val message = Message.obtain()
                message.data = bundle
                MainScope().launch {
                    SerialPort.receivedDataCallback?.invoke(receivedData)
                }

                flag = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SerialPort.logUtil.log("SerialPortService","Destroy")
        SerialPort.bluetoothSocket?.remoteDevice?.connectGatt(
                this,
                false,
                SerialPortConnect.bluetoothGattCallback
        )?.disconnect()
        SerialPort.bluetoothSocket?.close()
        SerialPort.connectCallback?.invoke()
        SerialPort.connectedDevice?.let {
            SerialPort.connectStatusCallback?.invoke(false, it)
        }
        SerialPort.connectedDevice = null
        SerialPort.connectStatus = false
        ToastUtil.toast(this,SerialPortToast.disconnect)
    }
}