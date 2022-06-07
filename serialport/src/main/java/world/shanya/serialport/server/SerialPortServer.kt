package world.shanya.serialport.server

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.SerialPortTools
import world.shanya.serialport.tools.ToastUtil

typealias ServerReceivedDataCallback = (data: String) -> Unit
typealias ServerConnectStatusCallback = (status: Boolean, bluetoothDevice: BluetoothDevice?) -> Unit


@SuppressLint("MissingPermission")
class SerialPortServer internal constructor(config: Config, context: Context) {

    class Config {
        internal var serverName = "SerialPortServer"
        internal var serverUUID = "00001101-0000-1000-8000-00805F9B34FB"
    }

    companion object {
        internal var serverName = "SerialPortServer"
        internal var serverUUID = "00001101-0000-1000-8000-00805F9B34FB"
        internal var serialPortBluetoothServerSocket: BluetoothSocket ?= null
        internal val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        internal var serialPortServerThread: SerialPortServerThread ?= null
        internal var connectStatus = false
        internal var connectedDevice: BluetoothDevice ?= null
        internal var serverReceivedDataCallback: ServerReceivedDataCallback ?= null
        internal var serverConnectStatusCallback: ServerConnectStatusCallback? = null

        @SuppressLint("StaticFieldLeak")
        private var serverContext: Context ?= null
        
        internal fun _setServerReceivedDataCallback(serverReceivedDataCallback: ServerReceivedDataCallback) {
            this.serverReceivedDataCallback = serverReceivedDataCallback
        }

        internal fun _setServerConnectStatusCallback(serverConnectStatusCallback: ServerConnectStatusCallback) {
            this.serverConnectStatusCallback = serverConnectStatusCallback
        }

        internal fun connected(device: BluetoothDevice) {
            LogUtil.log("Server has connected.")
            connectStatus = true
            serverConnectStatusCallback?.invoke(true, device)
            connectedDevice = device
            if (serialPortServerThread != null) {
                serialPortServerThread?.cancel()
                serialPortServerThread = null
            }
            MainScope().launch {
                serverContext?.let {
                    ToastUtil.toast(it, SerialPortToast.connectSucceeded)
                    it.startService(Intent(it, SerialPortServerService::class.java))
                }
            }
        }

        fun __disconnect() {
            serverContext?.let {
                it.stopService(Intent(it, SerialPortServerService::class.java))
            }
        }

        internal fun _disconnect() {
            connectedDevice?.let {
                serialPortBluetoothServerSocket?.close()
                connectStatus = false
                connectedDevice = null
                serverConnectStatusCallback?.invoke(false, null)
                serverContext?.let { context: Context ->
                    ToastUtil.toast(context, SerialPortToast.disconnect)
                }
            }
        }


    }

    init {
        if (!bluetoothAdapter.isEnabled) {
            val res = bluetoothAdapter.enable()
            if (res) {
                LogUtil.log("蓝牙打开成功")
                serverContext?.let {
                    ToastUtil.toast(it, SerialPortToast.openBluetoothSucceeded)
                }
            } else {
                LogUtil.log("蓝牙打开失败")
                serverContext?.let {
                    ToastUtil.toast(it, SerialPortToast.openBluetoothFailed)
                }
            }
        }
        serverContext = context
        serverName = config.serverName
        serverUUID = config.serverUUID
        val intentFilterConnection = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        intentFilterConnection.priority = Int.MAX_VALUE
        context.registerReceiver(SerialPort.bluetoothStatusBroadcastReceiver, intentFilterConnection)
    }

    fun setServerDiscoverable(status: Boolean) {
        if (status) {
            SerialPortTools.setDiscoverableTimeout()
        } else {
            SerialPortTools.closeDiscoverableTimeout()
        }
    }
    
    fun setServerReceivedDataCallback(serverReceivedDataCallback: ServerReceivedDataCallback) {
        _setServerReceivedDataCallback(serverReceivedDataCallback)
    }

    fun setServerConnectStatusCallback(serverConnectStatusCallback: ServerConnectStatusCallback) {
        _setServerConnectStatusCallback(serverConnectStatusCallback)
    }

    fun openServer() {
        serialPortServerThread = SerialPortServerThread()
        serialPortServerThread?.start()
        setServerDiscoverable(true)
        serverContext?.let {
            ToastUtil.toast(it, SerialPortToast.openServer)
        }
    }

    fun disconnect() {
        serverContext?.let {
            it.stopService(Intent(it, SerialPortServerService::class.java))
        }
    }

    fun closeServer() {
        if (serialPortServerThread != null) {
            serialPortServerThread?.cancel()
            serialPortServerThread = null
        }
        if (connectStatus) {
            disconnect()
        }
        setServerDiscoverable(false)
        serverContext?.let {
            ToastUtil.toast(it, SerialPortToast.closeServer)
        }
    }

    private fun send(data : String){
        if (connectStatus) {
            try {
                val outputStream = serialPortBluetoothServerSocket?.outputStream
                val bos: ByteArray =
                    SerialPortTools.string2bytes(data, "GBK")
                outputStream?.write(bos)
                LogUtil.log("SerialPortServer", "发送数据: $data")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            serverContext?.let {
                ToastUtil.toast(it, SerialPortToast.connectFirst)
            }
        }

    }

    fun sendData(data: String) {
        Thread {
            send(data)
        }.start()
    }

}