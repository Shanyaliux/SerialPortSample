/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.serialport
 * 类名：SerialPortServerThread.kt
 * 作者：Shanya
 * 日期：2022/3/7 下午3:27
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialport.server

import android.annotation.SuppressLint
import android.bluetooth.BluetoothServerSocket
import android.util.Log
import world.shanya.serialport.tools.LogUtil
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class SerialPortServerThread : Thread() {
    private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        SerialPortServer.bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(
            SerialPortServer.serverName,
            UUID.fromString(SerialPortServer.serverUUID)
        )
    }

    override fun run() {
        super.run()
        var shouldLoop = true
        while (shouldLoop) {
            SerialPortServer.serialPortBluetoothServerSocket = try {
                mmServerSocket?.accept()
            } catch (e: IOException) {
                LogUtil.log("Socket's accept() method failed", e.toString())
                shouldLoop = false
                null
            }

            SerialPortServer.serialPortBluetoothServerSocket?.also {
                SerialPortServer.connected(it.remoteDevice)
                mmServerSocket?.close()
                shouldLoop = false
            }
        }
    }

    fun cancel() {
        try {
            mmServerSocket?.close()
        } catch (e: IOException) {
            LogUtil.log("Could not close the connect socket", e.toString())
        }
    }

}