package world.shanya.serialport.connect

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import world.shanya.serialport.SerialPort

class ConnectionBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                SerialPort.logUtil.log("断开连接")
                SerialPort._legacyDisconnect()
            }
        }
    }
}