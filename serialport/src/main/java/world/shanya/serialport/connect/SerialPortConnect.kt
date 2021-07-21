package world.shanya.serialport.connect

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.discovery.Device
import world.shanya.serialport.service.SerialPortService
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.tools.SerialPortTools
import world.shanya.serialport.tools.ToastUtil
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * SerialPortConnect 连接管理类
 * @UpdateContent
 * 1. 部分代码重构
 * 2. 新增对BLE设备的支持
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
internal object SerialPortConnect {

    val gattServiceData = ArrayList<HashMap<String, String>>()
    val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String>>>()
    val mGattCharacteristics = ArrayList<ArrayList<BluetoothGattCharacteristic>>()
    var gattCharacteristic: BluetoothGattCharacteristic? = null

    internal var bluetoothGatt: BluetoothGatt? = null


    /**
     * bluetoothGattCallback BLE设备连接回调
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            //BLE设备连接成功
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectedResult(SerialPort.newContext, true, gatt, gatt?.device)
            }

            //BLE设备断开连接
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (SerialPort.connectedDevice == null) {
                    connectedResult(SerialPort.newContext, false, gatt, gatt?.device)
                }else {
                    disconnectResult(SerialPort.newContext)
                    gatt?.close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt?.services?.let {
                    for (gattService in it) {
                        val currentServiceData = HashMap<String, String>()
                        var uuid = gattService.uuid.toString()
                        gattServiceData.add(currentServiceData)
                        SerialPort.logUtil.log("UUID1-> $uuid")
                        val gattCharacteristicGroupData =
                            java.util.ArrayList<HashMap<String, String>>()
                        val gattCharacteristics = gattService.characteristics
                        val charas = ArrayList<BluetoothGattCharacteristic>()
                        for (gattc in gattCharacteristics) {
                            charas.add(gattc)
                            val currentChData = HashMap<String, String>()
                            uuid = gattc.uuid.toString()
                            SerialPort.logUtil.log("UUID2-> $uuid")
                            if (uuid == SerialPort.UUID_BLE) {
                                gattCharacteristic = gattc
                                val buff = "hello BLE\r\n"
                                SerialPortTools.bleSendData(gatt,gattc,buff)
                            }
                        }
                    }
                }

            }
            gatt?.setCharacteristicNotification(gattCharacteristic,true)

        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                SerialPort.logUtil.log("发送成功")

            }
            else if (status == BluetoothGatt.GATT_FAILURE) {
                SerialPort.logUtil.log("发送失败")
            }
        }


        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val value = characteristic?.value
            if (value != null && value.isNotEmpty()) {
                SerialPort.logUtil.log("555")
                val newValues = String(value)
                SerialPort.logUtil.log("收到数据", newValues)
            }

        }
    }

    /**
     * connectBle 连接BLE设备
     * @param context 上下文
     * @param address 设备地址
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun connectBle(context: Context, address: String) {
        val bluetoothDevice =
            SerialPort.bluetoothAdapter.getRemoteDevice(address)
        bluetoothDevice.connectGatt(context, false, bluetoothGattCallback)
    }

    /**
     * connectLegacy 连接传统设备（处理大逻辑结构）
     * @param context 上下文
     * @param address 设备地址
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun connectLegacy(context: Context, address: String) {
        Thread {
            SerialPort.bluetoothSocket?.isConnected?.let { bluetoothSocketIsConnected ->
                if (bluetoothSocketIsConnected) {
                    SerialPort.connectCallback?.invoke()
                    if (SerialPort.autoConnectFlag) {
                        MainScope().launch {
                            ToastUtil.toast(context, SerialPortToast.disconnectFirst)
                        }
                    }
                } else {
                    _connectLegacy(context, address)
                }
            } ?: _connectLegacy(context, address)
        }.start()
    }


    /**
     * _connectLegacy 连接传统设备（处理具体连接细节）
     * @param context 上下文
     * @param address 设备地址
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    private fun _connectLegacy(context: Context, address: String) {
        var bluetoothDevice:BluetoothDevice ?= null
        try {
            bluetoothDevice =
                SerialPort.bluetoothAdapter.getRemoteDevice(address)
            SerialPort.logUtil.log("a", bluetoothDevice.name)
            val device =
                Device(bluetoothDevice.name ?: "", bluetoothDevice.address, bluetoothDevice.type)
            SerialPort.bluetoothSocket =
                bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(SerialPort.UUID))
            SerialPort.bluetoothSocket?.connect()

            connectedResult(context, true, null, bluetoothDevice)

            SerialPort.inputStream = SerialPort.bluetoothSocket?.inputStream
            context.startService(Intent(context, SerialPortService::class.java))
        } catch (e: IOException) {
            e.printStackTrace()

            connectedResult(context, false, null, bluetoothDevice)
            try {
                SerialPort.bluetoothSocket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * bleDisconnect BLE设备断开连接
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun bleDisconnect() {
        bluetoothGatt?.disconnect()
    }

    /**
     * legacyDisconnect 传统设备断开连接
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun legacyDisconnect(context: Context) {
        context.stopService(Intent(context, SerialPortService::class.java))
    }

    /**
     * connectedResult 连接结果报告
     * @param context 上下文
     * @param result 连接结构
     * @param gatt gatt
     * @param bluetoothDevice 连接设备
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    private fun connectedResult(
        context: Context?,
        result: Boolean,
        gatt: BluetoothGatt?,
        bluetoothDevice: BluetoothDevice?
    ) {
        if (result) {
            if (bluetoothDevice?.type == 2) {
                bluetoothGatt = gatt
                bluetoothGatt?.discoverServices()
            }
            val device = Device(
                bluetoothDevice?.name ?: "",
                bluetoothDevice?.address ?: "",
                bluetoothDevice?.type ?: 1)
            SerialPort.connectStatus = true
            SerialPort.connectCallback?.invoke()
            SerialPort.connectStatusCallback?.invoke(true, device)
            SerialPort.connectionStatusCallback?.invoke(true, bluetoothDevice)
            SerialPort.connectedDevice = bluetoothDevice
            if (bluetoothDevice?.type == 2) {
                SerialPort.logUtil.log("SerialPort", "连接BLE设备成功")
            } else {
                SerialPort.logUtil.log("SerialPort", "连接传统设备成功")
            }
            MainScope().launch {
                context?.let {
                    ToastUtil.toast(it, SerialPortToast.connectSucceeded)
                }

            }
        } else {
            SerialPort.connectCallback?.invoke()
            SerialPort.logUtil.log("SerialPort", "连接失败")
            MainScope().launch {
                context?.let {
                    ToastUtil.toast(it, SerialPortToast.connectFailed)
                }
            }
        }

    }

    /**
     * disconnectResult 断开结果报告
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun disconnectResult(context: Context?) {
        if (SerialPort.connectedDevice?.type == 1) {
            SerialPort.bluetoothSocket?.close()
        }
        SerialPort.connectCallback?.invoke()
        SerialPort.connectedDevice?.let {
            val device = Device(it.name, it.address, it.type)
            SerialPort.connectStatusCallback?.invoke(false, device)
            SerialPort.connectionStatusCallback?.invoke(false, it)
            SerialPort.connectStatus = false
            SerialPort.connectedDevice = null
            context?.let {context ->
                ToastUtil.toast(context,SerialPortToast.disconnect)
            }
            if (it.type == 2) {
                SerialPort.logUtil.log("断开BLE设备连接")
            }else {
                SerialPort.logUtil.log("断开传统设备连接")
            }

        }
    }
}