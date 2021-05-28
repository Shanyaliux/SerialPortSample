package world.shanya.serialport.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import world.shanya.serialport.SerialPort

/**
 * DiscoveryBroadcastReceiver 蓝牙搜索状态广播接收器
 * @Author Shanya
 * @Date 2021-5-28
 * @Version 3.1.0
 */
class DiscoveryBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    if (SerialPort.ignoreNoNameDeviceFlag) {
                        if (device.name != null) {
                            val tempDevice = Device(device.name, device.address, false)
                            addDevice(tempDevice)
                        }
                    } else {
                        val tempDevice = Device(device.name ?: "", device.address,false)
                        addDevice(tempDevice)
                    }
                }
            }

            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                SerialPort.logUtil.log("扫描传统蓝牙设备","开始搜索")
                SerialPort.discoveryStatusLiveData.value = true
            }

            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                SerialPort.logUtil.log("扫描传统蓝牙设备","停止搜索")
                SerialPortDiscovery.stopBleScan()
                SerialPort.discoveryStatusLiveData.value = false
            }
        }
    }

    /**
    * 添加传统蓝牙设备
    * @param device 设备
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
    private fun addDevice(device: Device) {
        if (!SerialPort.unPairedDevicesList.contains(device)) {
            SerialPort.logUtil.log(
                    "找到传统蓝牙设备",
                    "设备名：${device.name}  设备地址：${device.address}")
            SerialPort.unPairedDevicesList.add(device)
            SerialPort.findUnpairedDeviceCallback?.invoke()
        }
    }

}