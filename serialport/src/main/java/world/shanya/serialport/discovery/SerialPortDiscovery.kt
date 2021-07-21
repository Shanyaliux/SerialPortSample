package world.shanya.serialport.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import world.shanya.serialport.SerialPort
import world.shanya.serialport.tools.LogUtil
import java.lang.Exception

//找到设备接口
typealias FindUnpairedDeviceCallback = () -> Unit

/**
 * SerialPortDiscovery 搜索管理类
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
internal object SerialPortDiscovery {


    //搜索状态LiveData
    internal var discoveryStatusLiveData = MutableLiveData<Boolean>()
    //已配对设备列表
    internal val pairedDevicesListBD = ArrayList<BluetoothDevice>()
    //未配对设备列表
    internal val unPairedDevicesListBD = ArrayList<BluetoothDevice>()

    @Deprecated(message = "该变量在4.0.0版本开始被弃用",replaceWith = ReplaceWith(expression = "pairedDevicesListBD"))
    internal val pairedDevicesList = ArrayList<Device>()
    @Deprecated(message = "该变量在4.0.0版本开始被弃用",replaceWith = ReplaceWith(expression = "unPairedDevicesListBD"))
    internal val unPairedDevicesList = ArrayList<Device>()
    /**
     * 搜索 BLE 回调
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            result?.let {scanResult ->
                scanResult.device?.let {
                    if (SerialPort.ignoreNoNameDeviceFlag) {
                        if (it.name != null) {
                            addBleDevice(it)
                            val device = Device(it.name, it.address, it.type)
                            val deviceL = Device(it.name, it.address, it.type)
                            addBleDevice(device, deviceL)
                        }
                    } else {
                        addBleDevice(it)
                        val device = Device(it.name ?: "", it.address, it.type)
                        val deviceL = Device(it.name ?: "", it.address, it.type)
                        addBleDevice(device, deviceL)
                    }
                }
            }
        }
    }

    /**
    * 添加 BLE 设备
    * @param device BLE设备
    * @Author Shanya
    * @Date 2021-7-21
    * @Version 4.0.0
    */
    private fun addBleDevice(device: BluetoothDevice) {
        if (!unPairedDevicesListBD.contains(device) && !pairedDevicesListBD.contains(device)) {
            unPairedDevicesListBD.add(device)
            LogUtil.log("找到BLE蓝牙设备","设备名：${device.name}  设备地址：${device.address}")
            SerialPort.findUnpairedDeviceCallback?.invoke()
        }

    }

    /**
     * 添加 BLE 设备
     * @param device BLE设备
     * @param deviceL 传统设备
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    private fun addBleDevice(device: Device, deviceL: Device) {
        if (unPairedDevicesList.contains(deviceL)) {
            unPairedDevicesList.remove(deviceL)
        }
        if (!unPairedDevicesList.contains(device)) {
            unPairedDevicesList.add(device)
            LogUtil.log("找到BLE蓝牙设备","设备名：${device.name}  设备地址：${device.address}")
            SerialPort.findUnpairedDeviceCallback?.invoke()
        }

    }

    /**
    * 开始 BLE 设备扫描
    * @param
    * @Author Shanya
    * @Date 2021-7-21
    * @Version 4.0.0
    */
    internal fun startBleScan() {
        LogUtil.log("开始搜索BLE蓝牙设备")
        SerialPort.bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
    }

    /**
     * 停止 BLE 设备扫描
     * @param
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun stopBleScan() {
        LogUtil.log("停止搜索BLE蓝牙设备")
        SerialPort.bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
    }

    /**
     * 搜索传统蓝牙设备
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun startLegacyScan(context: Context) {
        LogUtil.log("扫描传统蓝牙设备","注册传统蓝牙广播")
        context.registerReceiver(SerialPort.discoveryBroadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        context.registerReceiver(SerialPort.discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        context.registerReceiver(SerialPort.discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
        LogUtil.log("扫描传统蓝牙设备","获取已配对设备")
        val pairedDevices:Set<BluetoothDevice> = SerialPort.bluetoothAdapter.bondedDevices
        if (pairedDevices.isNotEmpty()){
            pairedDevicesListBD.clear()
            pairedDevicesList.clear()
            for (device in pairedDevices){
                pairedDevicesListBD.add(device)
                pairedDevicesList.add(Device(device.name,device.address,device.type))
            }
        }

        unPairedDevicesListBD.clear()
        unPairedDevicesList.clear()
        SerialPort.bluetoothAdapter.startDiscovery()
    }

    /**
     * 停止传统蓝牙设备
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun stopLegacyScan(context: Context) {
        LogUtil.log("扫描传统蓝牙设备","停止搜索")
        try {
            context.unregisterReceiver(SerialPort.discoveryBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SerialPort.bluetoothAdapter.cancelDiscovery()
    }
}