/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.serialport
 * 类名：SerialPortServer.kt
 * 作者：Shanya
 * 日期：2022/3/7 下午3:18
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialport

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.server.SerialPortServerService
import world.shanya.serialport.server.SerialPortServerThread
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.SerialPortTools
import world.shanya.serialport.tools.ToastUtil

typealias ServerReceivedDataCallback = (data: String) -> Unit
typealias ServerConnectStatusCallback = (status: Boolean) -> Unit

class SerialPortServer constructor(private val context: Context) {
    companion object {
        internal var serverName = "SerialPortServer"
        internal var serverUUID = "00001101-0000-1000-8000-00805F9B34FB"
        internal var serialPortBluetoothServerSocket: BluetoothSocket ?= null
        internal val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        internal var serialPortServerThread:SerialPortServerThread ?= null
        internal var connectStatus = false
        internal var serverReceivedDataCallback: ServerReceivedDataCallback ?= null
        @SuppressLint("StaticFieldLeak")
        private var serverContext: Context ?= null
        
        internal fun _setServerReceivedDataCallback(serverReceivedDataCallback: ServerReceivedDataCallback) {
            this.serverReceivedDataCallback = serverReceivedDataCallback
        }

        internal fun connected() {
            LogUtil.log("Server has connected.")
            connectStatus = true
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
    }

    init {
        serverContext = context
    }
    
    fun setServerReceivedDataCallback(serverReceivedDataCallback: ServerReceivedDataCallback) {
        _setServerReceivedDataCallback(serverReceivedDataCallback)
    }

    fun openServer() {
        serialPortServerThread = SerialPortServerThread()
        serialPortServerThread?.start()
    }

    private fun send(data : String){
        try {
            val outputStream = serialPortBluetoothServerSocket?.outputStream
            val bos: ByteArray =
                SerialPortTools.string2bytes(data, "GBK")
            outputStream?.write(bos)
            LogUtil.log("SerialPortServer","发送数据: $data")
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun sendData(data: String) {
        Thread {
            send(data)
        }.start()
    }

}