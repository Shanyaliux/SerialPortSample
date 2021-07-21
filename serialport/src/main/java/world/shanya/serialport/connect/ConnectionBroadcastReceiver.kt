package world.shanya.serialport.connect

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import world.shanya.serialport.SerialPort

/**
 * ConnectionBroadcastReceiver 蓝牙连接状态变更广播接收器
 * 在这里只对传统蓝牙的断开进行处理
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
class ConnectionBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                if (SerialPort.connectedDevice?.type == 1) {
                    SerialPort._legacyDisconnect()
                }
            }
        }
    }
}