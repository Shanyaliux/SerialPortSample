package world.shanya.serialport

import android.content.Context
import world.shanya.serialport.tools.SPUtil

object SerialPortBuilder {
    private val serialPort = SerialPort.get()
    private var isAutoConnect = false
    fun isDebug(status: Boolean): SerialPortBuilder {
        serialPort.isDebug(status)
        return this
    }
    fun autoConnect(status: Boolean): SerialPortBuilder {
        isAutoConnect = status
        return this
    }
    fun setReadDataType(type: Int): SerialPortBuilder {
        SerialPort.readDataType = type
        return this
    }
    fun setSendDataType(type: Int): SerialPortBuilder {
        SerialPort.sendDataType = type
        return this
    }
    fun build(context: Context): SerialPort {
        serialPort.build(context)
        if (isAutoConnect) {
            SPUtil.getSPDevice(context)?.let { SerialPort.connectDevice(it) }
        }
        return serialPort
    }
}