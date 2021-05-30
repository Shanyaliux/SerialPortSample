package world.shanya.serialport.broadcast

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import java.io.IOException

/**
 * ConnectBroadcastReceiver 蓝牙连接状态广播接收器
 * @Author Shanya
 * @Date 2021-3-16
 * @Version 3.0.0
 */
class ConnectBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                SerialPort.logUtil.log("ConnectBroadcastReceiver","Disconnect")
                try {
                    SerialPort.bluetoothSocket?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
//                SerialPort.connectCallback?.invoke()
//                SerialPort.connectedDevice?.let {
//                    SerialPort.connectStatusCallback?.invoke(false, it)
//                }
//                SerialPort.connectedDevice = null
//                SerialPort.connectStatus = false
//                Toast.makeText(context,"断开连接", Toast.LENGTH_SHORT).show()
            }
        }
    }
}