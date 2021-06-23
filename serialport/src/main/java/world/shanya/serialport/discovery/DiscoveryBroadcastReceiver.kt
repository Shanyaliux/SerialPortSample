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
                    if (device.type != 2) {
                        if (SerialPort.ignoreNoNameDeviceFlag) {
                            if (device.name != null) {
                                addDevice(device)
                            }
                        } else {
                            addDevice(device)
                        }
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
    private fun addDevice(device: BluetoothDevice) {
        if (!SerialPort.unPairedDevicesListBD.contains(device) && !SerialPort.pairedDevicesListBD.contains(device)) {
            SerialPort.logUtil.log(
                    "找到传统蓝牙设备",
                    "设备名：${device.name}  设备地址：${device.address}")
            SerialPort.unPairedDevicesListBD.add(device)
            SerialPort.unPairedDevicesList.add(Device(device.name,device.address,device.type))
            SerialPort.findUnpairedDeviceCallback?.invoke()
        }
    }

}