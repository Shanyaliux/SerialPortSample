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
                SerialPort.connectCallback?.invoke()
                SerialPort.connectStatus = false
                Toast.makeText(context,"断开连接", Toast.LENGTH_SHORT).show()
            }
        }
    }
}