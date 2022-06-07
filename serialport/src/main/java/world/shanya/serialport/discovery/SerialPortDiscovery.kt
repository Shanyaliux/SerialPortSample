package world.shanya.serialport.discovery

import android.annotation.SuppressLint
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
typealias DiscoveryStatusCallback = (status: Boolean) -> Unit
typealias DiscoveryStatusWithTypeCallback = (deviceType: Int, status: Boolean) -> Unit

/**
 * SerialPortDiscovery 搜索管理类
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
@SuppressLint("MissingPermission")
internal object SerialPortDiscovery {

    //搜索状态LiveData
    internal var discoveryStatusLiveData = MutableLiveData<Boolean>()
    //已配对设备列表
    internal val pairedDevicesListBD = ArrayList<BluetoothDevice>()
    //未配对设备列表
    internal val unPairedDevicesListBD = ArrayList<BluetoothDevice>()
    //搜索状态带类型回调
    internal var discoveryStatusWithTypeCallback: DiscoveryStatusWithTypeCallback ?= null
    //搜索状态回调
    internal var discoveryStatusCallback: DiscoveryStatusCallback ?= null

    @Deprecated(message = "该变量在4.0.0版本开始被弃用",replaceWith = ReplaceWith(expression = "pairedDevicesListBD"))
    internal val pairedDevicesList = ArrayList<Device>()
    @Deprecated(message = "该变量在4.0.0版本开始被弃用",replaceWith = ReplaceWith(expression = "unPairedDevicesListBD"))
    internal val unPairedDevicesList = ArrayList<Device>()
    /**
     * 搜索 BLE 回调
     * @Author Shanya
     * @Date 2021-8-13
     * @Version 4.0.3
     */
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            result?.let {scanResult ->
                scanResult.device?.let {
                    if (SerialPort.ignoreNoNameDeviceFlag) {
                        if (it.name != null) {
                            addDevice(it)
                        }
                    } else {
                        addDevice(it)
                    }
                }
            }
        }
    }

    /**
     * 添加蓝牙设备
     * @param bluetoothDevice 设备
     * @Author Shanya
     * @Date 2021-8-13
     * @Version 4.0.3
     */
    internal fun addDevice(bluetoothDevice: BluetoothDevice) {
        if (!unPairedDevicesListBD.contains(bluetoothDevice) && !pairedDevicesListBD.contains(bluetoothDevice)) {
            unPairedDevicesListBD.add(bluetoothDevice)
            LogUtil.log("找到蓝牙设备","设备名：${bluetoothDevice.name}  " +
                    "设备地址：${bluetoothDevice.address}  " +
                    "设备类型：${bluetoothDevice.type}")
            unPairedDevicesList.add(Device(bluetoothDevice.name ?:"",
                bluetoothDevice.address?:"",bluetoothDevice.type))
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
        discoveryStatusWithTypeCallback?.invoke(SerialPort.DISCOVERY_BLE, true)
        discoveryStatusCallback?.invoke(true)
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
        discoveryStatusWithTypeCallback?.invoke(SerialPort.DISCOVERY_BLE, false)
        discoveryStatusCallback?.invoke(false)
    }

    /**
     * 搜索传统蓝牙设备
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun startLegacyScan(context: Context) {
        LogUtil.log("注册传统蓝牙扫描结果广播接收器")
        context.registerReceiver(SerialPort.discoveryBroadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        context.registerReceiver(SerialPort.discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        context.registerReceiver(SerialPort.discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
        LogUtil.log("获取已配对设备")
        val pairedDevices:Set<BluetoothDevice> = SerialPort.bluetoothAdapter.bondedDevices
        if (pairedDevices.isNotEmpty()){
            pairedDevicesListBD.clear()
            pairedDevicesList.clear()
            for (device in pairedDevices){
                pairedDevicesListBD.add(device)
                pairedDevicesList.add(Device(device.name ?:"",device.address?:"",device.type))
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
     * @Date 2021-8-13
     * @Version 4.0.3
     */
    internal fun stopLegacyScan(context: Context) {
        LogUtil.log("停止搜索传统蓝牙设备")
        try {
            context.unregisterReceiver(SerialPort.discoveryBroadcastReceiver)
        } catch (e: Exception) {

        }
        SerialPort.bluetoothAdapter.cancelDiscovery()
    }
}