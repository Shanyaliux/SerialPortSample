package world.shanya.serialport.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.IntentFilter
import world.shanya.serialport.SerialPort
import world.shanya.serialport.tools.LogUtil
import java.lang.Exception

internal object SerialPortDiscovery {

    /**
     * 搜索 BLE 回调
     * @Author Shanya
     * @Date 2021-5-24
     * @Version 3.1.0
     */
    internal val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            result?.let {scanResult ->
                scanResult.device?.let {
                    if (SerialPort.ignoreNoNameDeviceFlag) {
                        if (it.name != null) {
                            addBleDevice(it)
                        }
                    } else {
                        addBleDevice(it)
                    }
                }
            }
        }
    }

    /**
    * 添加 BLE 设备
    * @param device BLE设备
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
    private fun addBleDevice(device: BluetoothDevice) {
        if (!SerialPort.unPairedDevicesList.contains(device) && !SerialPort.pairedDevicesList.contains(device)) {
            SerialPort.unPairedDevicesList.add(device)
            SerialPort.logUtil.log("找到BLE蓝牙设备","设备名：${device.name}  设备地址：${device.address}")
            SerialPort.findUnpairedDeviceCallback?.invoke()
        }

    }

    /**
    * 开始 BLE 设备扫描
    * @param
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
    internal fun startBleScan() {
        SerialPort.logUtil.log("扫描BLE蓝牙设备","开始搜索")
        SerialPort.bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
    }

    /**
     * 停止 BLE 设备扫描
     * @param
     * @Author Shanya
     * @Date 2021/5/28
     * @Version 3.1.0
     */
    internal fun stopBleScan() {
        SerialPort.logUtil.log("扫描BLE蓝牙设备","停止搜索")
        SerialPort.bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
    }

    /**
     * 搜索传统蓝牙设备
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-5-28
     * @Version 3.1.0
     */
    fun startLegacyScan(context: Context) {
        SerialPort.logUtil.log("扫描传统蓝牙设备","注册传统蓝牙广播")
        context.registerReceiver(SerialPort.discoveryBroadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        context.registerReceiver(SerialPort.discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        context.registerReceiver(SerialPort.discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
        SerialPort.logUtil.log("扫描传统蓝牙设备","获取已配对设备")
        val pairedDevices:Set<BluetoothDevice> = SerialPort.bluetoothAdapter.bondedDevices
        if (pairedDevices.isNotEmpty()){
            SerialPort.pairedDevicesList.clear()
            for (device in pairedDevices){
                SerialPort.pairedDevicesList.add(device)
            }
        }

        SerialPort.unPairedDevicesList.clear()
        SerialPort.bluetoothAdapter.startDiscovery()
    }

    /**
     * 停止传统蓝牙设备
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-5-28
     * @Version 3.1.0
     */
    fun stopLegacyScan(context: Context) {
        SerialPort.logUtil.log("扫描传统蓝牙设备","停止搜索")
        try {
            context.unregisterReceiver(SerialPort.discoveryBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        SerialPort.bluetoothAdapter.cancelDiscovery()
    }
}