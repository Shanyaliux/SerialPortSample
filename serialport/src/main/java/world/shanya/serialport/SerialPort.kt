package world.shanya.serialport

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.broadcast.ConnectBroadcastReceiver
import world.shanya.serialport.discovery.Device
import world.shanya.serialport.discovery.DiscoveryActivity
import world.shanya.serialport.service.SerialPortService
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.SPUtil
import java.io.IOException
import java.io.InputStream
import java.util.UUID.*
import kotlin.collections.ArrayList

typealias FindUnpairedDeviceCallback = () -> Unit
typealias ConnectStatusCallback = (status: Boolean, device: Device) -> Unit

class SerialPort private constructor() {
    companion object {
        private var instance: SerialPort? = null
            get() {
                if (field == null) {
                    field = SerialPort()
                }
                return field
            }

        @Synchronized
        fun get(): SerialPort {
            return instance!!
        }

        val pairedDevicesList = ArrayList<Device>()
        val unPairedDevicesList = ArrayList<Device>()
        internal val logUtil = LogUtil("SerialPortDebug")


        const val READ_STRING = 1
        const val SEND_STRING = 2
        const val READ_HEX = 3
        const val SEND_HEX = 4
        internal var readDataType = READ_STRING
        internal var sendDataType = SEND_STRING
        internal val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        internal var bluetoothSocket: BluetoothSocket?= null
        internal var inputStream: InputStream?= null
        private var UUID = "00001101-0000-1000-8000-00805F9B34FB"
        internal var discoveryLiveData = MutableLiveData<Boolean>()
        private var newContext: Context ?= null
        private var oldContext: Context ?= null
        private val connectBroadcastReceiver = ConnectBroadcastReceiver()
        internal var connectStatus = false

        internal var findUnpairedDeviceCallback: FindUnpairedDeviceCallback ?= null
        fun setFindDeviceListener(findUnpairedDeviceCallback: FindUnpairedDeviceCallback) {
            this.findUnpairedDeviceCallback = findUnpairedDeviceCallback
        }

        internal var _connectStatusCallback: ConnectStatusCallback ?= null
        internal fun set_ConnectStatusListener(connectStatusCallback: ConnectStatusCallback) {
            _connectStatusCallback = connectStatusCallback
        }

        internal fun connectDevice(device: Device) {
            Thread {
                val address = device.address
                val bluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
                bluetoothSocket?.isConnected?.let {
                    if (it) {
                        _connectStatusCallback?.invoke(true,device)
                        MainScope().launch {
                            Toast.makeText(newContext, "请先断开当前连接", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        try {
                            bluetoothSocket =
                                    bluetoothDevice.createRfcommSocketToServiceRecord(fromString(UUID))
                            bluetoothSocket?.connect()

                            newContext?.let { SPUtil.putString(it,device) }
                            _connectStatusCallback?.invoke(true,device)
                            connectStatus = true
                            logUtil.log("SerialPort","连接成功")
                            MainScope().launch {
                                Toast.makeText(newContext,"连接成功", Toast.LENGTH_SHORT).show()
                            }

                            inputStream = bluetoothSocket?.inputStream
                            newContext?.startService(Intent(newContext,SerialPortService::class.java))
                        } catch (e: IOException) {
                            MainScope().launch {
                                Toast.makeText(newContext,"连接失败", Toast.LENGTH_SHORT).show()
                            }
                            connectStatus = false
                            _connectStatusCallback?.invoke(false,device)
                            try {
                                bluetoothSocket?.close()
                            }catch (e: IOException){
                                e.printStackTrace()
                            }
                        }
                    }
                }?: let {
                    try {
                        bluetoothSocket =
                                bluetoothDevice.createRfcommSocketToServiceRecord(fromString(UUID))
                        bluetoothSocket?.connect()

                        newContext?.let { SPUtil.putString(it,device) }
                        connectStatus = true
                        _connectStatusCallback?.invoke(true,device)
                        logUtil.log("SerialPort","连接成功")
                        MainScope().launch {
                            Toast.makeText(newContext,"连接成功", Toast.LENGTH_SHORT).show()
                        }

                        inputStream = bluetoothSocket?.inputStream

                        newContext?.startService(Intent(newContext,SerialPortService::class.java))
                    } catch (e: IOException) {
                        MainScope().launch {
                            Toast.makeText(newContext,"连接失败", Toast.LENGTH_SHORT).show()
                        }
                        connectStatus = false
                        _connectStatusCallback?.invoke(false,device)
                        try {
                            bluetoothSocket?.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                }
            }.start()

        }
    }

    init {
        discoveryLiveData.value = false
    }

    internal fun isDebug(status: Boolean):SerialPort {
        logUtil.status = status
        return this
    }

    internal fun build(context: Context) {
        oldContext = newContext
        newContext = context
        oldContext?.unregisterReceiver(connectBroadcastReceiver)
        newContext?.registerReceiver(
            connectBroadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        )
    }

    fun openDiscoveryActivity() {
        newContext?.startActivity(Intent(newContext,DiscoveryActivity::class.java))
    }

    fun disconnect() {
        try {
            connectStatus = false
            newContext?.stopService(Intent(newContext,SerialPortService::class.java))

        }catch (e: IOException){
            e.printStackTrace()
        }
    }
}