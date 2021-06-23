package world.shanya.serialport.connect

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.discovery.Device
import world.shanya.serialport.service.SerialPortService
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.tools.ToastUtil
import java.io.IOException
import java.util.*

internal object SerialPortConnect {

    internal val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            SerialPort.logUtil.log("onConnectionStateChange",gatt?.device?.name.toString())
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                val device = Device(gatt?.device?.name?:"",gatt?.device?.address?:"",gatt?.device?.type?:0)
                SerialPort.connectStatus = true
                SerialPort.connectCallback?.invoke()
                SerialPort.connectStatusCallback?.invoke(true,gatt?.device)
                SerialPort.connectedDevice = gatt?.device
            }

            if (status == BluetoothGatt.STATE_DISCONNECTED) {
                gatt?.close()
            }

        }
    }

    internal fun connectBle(context: Context, address: String) {
        val bluetoothDevice =
            SerialPort.bluetoothAdapter.getRemoteDevice(address)
        val bluetoothGatt =
            bluetoothDevice.connectGatt(context, false, bluetoothGattCallback)
    }

    internal fun connectLegacy(context: Context, address: String) {
        Thread {
            SerialPort.bluetoothSocket?.isConnected?.let { bluetoothSocketIsConnected ->
                if (bluetoothSocketIsConnected) {
                    SerialPort.connectCallback?.invoke()
                    if (SerialPort.autoConnectFlag) {
                        MainScope().launch {
                            ToastUtil.toast(context,SerialPortToast.disconnectFirst)
                        }
                    }
                } else {
                    _connectLegacy(context,address)
                }
            } ?: _connectLegacy(context,address)
        }.start()
    }

    private fun _connectLegacy(context: Context, address: String) {
        try {
            val bluetoothDevice =
                SerialPort.bluetoothAdapter.getRemoteDevice(address)
            SerialPort.logUtil.log("a",bluetoothDevice.name)
            val device = Device(bluetoothDevice.name?:"",bluetoothDevice.address,bluetoothDevice.type)
            SerialPort.bluetoothSocket =
                bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(SerialPort.UUID))
            SerialPort.bluetoothSocket?.connect()

            //存储连接成功设备地址
            SerialPort.connectCallback?.invoke()
            SerialPort.connectStatusCallback?.invoke(true,bluetoothDevice)
            SerialPort.connectedDevice = bluetoothDevice
            SerialPort.connectStatus = true
            SerialPort.logUtil.log("SerialPort","连接成功")
            MainScope().launch {
                ToastUtil.toast(context,SerialPortToast.connectSucceeded)
            }

            SerialPort.inputStream = SerialPort.bluetoothSocket?.inputStream
            context.startService(Intent(context,SerialPortService::class.java))
        } catch (e: IOException) {
            e.printStackTrace()
            SerialPort.logUtil.log("SerialPort","连接失败")
            MainScope().launch {
                ToastUtil.toast(context,SerialPortToast.connectFailed)
            }
            SerialPort.connectStatus = false
            SerialPort.connectCallback?.invoke()
            try {
                SerialPort.bluetoothSocket?.close()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }


}