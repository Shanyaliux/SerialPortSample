package world.shanya.serialport.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import world.shanya.serialport.SerialPort
import world.shanya.serialport.tools.LogUtil

/**
 * DiscoveryBroadcastReceiver 蓝牙搜索状态广播接收器
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
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
                LogUtil.log("开始搜索传统蓝牙设备")
                SerialPortDiscovery.discoveryStatusLiveData.value = true
            }

            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                LogUtil.log("停止搜索传统蓝牙设备")
                SerialPortDiscovery.stopBleScan()
                SerialPortDiscovery.discoveryStatusLiveData.value = false
            }
        }
    }

    /**
    * 添加传统蓝牙设备
    * @param device 设备
    * @Author Shanya
    * @Date 2021-7-21
    * @Version 4.0.0
    */
    private fun addDevice(device: BluetoothDevice) {
        if (!SerialPortDiscovery.unPairedDevicesListBD.contains(device) &&
            !SerialPortDiscovery.pairedDevicesListBD.contains(device)) {
            LogUtil.log(
                    "找到传统蓝牙设备",
                    "设备名：${device.name}  设备地址：${device.address}")
            SerialPortDiscovery.unPairedDevicesListBD.add(device)
            SerialPortDiscovery.unPairedDevicesList.add(Device(device.name,device.address,device.type))
            SerialPort.findUnpairedDeviceCallback?.invoke()
        }
    }

}