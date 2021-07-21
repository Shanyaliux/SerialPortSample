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

internal object SerialPortConnect {

    val gattServiceData = ArrayList<HashMap<String, String>>()
    val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String>>>()
    val mGattCharacteristics = ArrayList<ArrayList<BluetoothGattCharacteristic>>()
    var gattCharacteristic: BluetoothGattCharacteristic? = null

    var bluetoothGatt: BluetoothGatt? = null


    fun dataSeparate(len: Int): MutableList<Int> {
        val lens = ArrayList<Int>(2)
        lens.add(len / 20)
        lens.add(len - 20 * lens[0])
        return lens
    }


    internal val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                SerialPort.logUtil.log("onConnectionStateChange", "STATE_CONNECTED")
                val device = Device(
                    gatt?.device?.name ?: "",
                    gatt?.device?.address ?: "",
                    gatt?.device?.type ?: 0
                )
                connectedResult(SerialPort.newContext, true, gatt, gatt?.device)
            }

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectedResult(SerialPort.newContext, false, gatt, gatt?.device)
                SerialPort.logUtil.log("onConnectionStateChange","STATE_DISCONNECTED")
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
                                SerialPort.logUtil.log("asdasdasdasdasd")
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

    internal fun connectBle(context: Context, address: String) {
        val bluetoothDevice =
            SerialPort.bluetoothAdapter.getRemoteDevice(address)
        bluetoothDevice.connectGatt(context, false, bluetoothGattCallback)
    }

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

            //存储连接成功设备地址

            connectedResult(context, true, null, bluetoothDevice)
//            SerialPort.connectCallback?.invoke()
//            SerialPort.connectStatusCallback?.invoke(true, bluetoothDevice)
//            SerialPort.connectedDevice = bluetoothDevice
//            SerialPort.connectStatus = true
//            SerialPort.logUtil.log("SerialPort", "连接成功")
//            MainScope().launch {
//                ToastUtil.toast(context, SerialPortToast.connectSucceeded)
//            }

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

    internal fun bleDisconnect() {
        bluetoothGatt?.disconnect()
    }

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

            SerialPort.connectStatus = true
            SerialPort.connectCallback?.invoke()
            SerialPort.connectStatusCallback?.invoke(true, bluetoothDevice)
            SerialPort.connectedDevice = bluetoothDevice
            SerialPort.logUtil.log("SerialPort", "连接成功")
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

    internal fun disconnectResult(context: Context?) {
        if (SerialPort.connectedDevice?.type == 1) {
            SerialPort.bluetoothSocket?.close()
        }
        SerialPort.connectCallback?.invoke()
        SerialPort.connectedDevice?.let {
            SerialPort.connectStatusCallback?.invoke(false, it)
        }
        SerialPort.connectStatus = false
        SerialPort.connectedDevice = null
        context?.let {
            ToastUtil.toast(it,SerialPortToast.disconnect)
        }
        SerialPort.logUtil.log("断开连接")
    }
}