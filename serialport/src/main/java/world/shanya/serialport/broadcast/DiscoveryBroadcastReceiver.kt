package world.shanya.serialport.broadcast

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import world.shanya.serialport.SerialPort
import world.shanya.serialport.discovery.Device

/**
 * DiscoveryBroadcastReceiver 蓝牙搜索状态广播接收器
 * @Author Shanya
 * @Date 2021-3-16
 * @Version 3.0.0
 */
class DiscoveryBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    val tempDevice = Device(device.name ?: "unknown", device.address)
                    if (!SerialPort.unPairedDevicesList.contains(tempDevice)) {
                        SerialPort.logUtil.log("DiscoveryBroadcastReceiver",
                            "Find Device  :  ${device.name?:"unknown"}")

                        SerialPort.unPairedDevicesList.add(tempDevice)
                        SerialPort.findUnpairedDeviceCallback?.invoke()
                    }
                }
            }

            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                SerialPort.logUtil.log("DiscoveryBroadcastReceiver","Start")
                SerialPort.discoveryStatusLiveData.value = true
            }

            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                SerialPort.logUtil.log("DiscoveryBroadcastReceiver","Finished")
                SerialPort.discoveryStatusLiveData.value = false
            }
        }
    }

}