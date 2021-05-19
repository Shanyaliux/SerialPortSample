package world.shanya.serialport.connect

import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.discovery.Device
import world.shanya.serialport.service.SerialPortService
import world.shanya.serialport.tools.SPUtil
import java.io.IOException
import java.util.*

object SerialPortConnect {

    internal fun connectBle(context: Context, address: String) {
        val bluetoothDevice =
            SerialPort.bluetoothAdapter.getRemoteDevice(address)
        val bluetoothGatt =
            bluetoothDevice.connectGatt(context, false, SerialPort.bluetoothGattCallback)
    }

    internal fun connectLegacy(context: Context, address: String) {
        Thread {
            SerialPort.bluetoothSocket?.isConnected?.let { bluetoothSocketIsConnected ->
                if (bluetoothSocketIsConnected) {
                    SerialPort.connectCallback?.invoke()
                    if (SerialPort.autoConnectFlag) {
                        MainScope().launch {
                            Toast.makeText(context, "请先断开当前连接", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    _connectLegacy(context,address)
                }

            } ?: let {
                _connectLegacy(context,address)
            }
        }.start()
    }

    private fun _connectLegacy(context: Context, address: String) {
        try {
            val bluetoothDevice =
                SerialPort.bluetoothAdapter.getRemoteDevice(address)
            val device = Device(bluetoothDevice.name?:"",bluetoothDevice.address,false)
            SerialPort.bluetoothSocket =
                bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(SerialPort.UUID))
            SerialPort.bluetoothSocket?.connect()

            //存储连接成功设备地址

            SerialPort.connectCallback?.invoke()
            SerialPort.connectStatusCallback?.invoke(true,device)
            SerialPort.connectedDevice = device
            SerialPort.connectStatus = true
            SerialPort.logUtil.log("SerialPort","连接成功")
            MainScope().launch {
                Toast.makeText(context,"连接成功", Toast.LENGTH_SHORT).show()
            }

            SerialPort.inputStream = SerialPort.bluetoothSocket?.inputStream
            context.startService(Intent(context,SerialPortService::class.java))
        } catch (e: IOException) {
            SerialPort.logUtil.log("SerialPort","连接失败")
            MainScope().launch {
                Toast.makeText(context,"连接失败", Toast.LENGTH_SHORT).show()
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