package world.shanya.serialport

import android.content.Context
import world.shanya.serialport.tools.SPUtil


/**
 * SerialPortBuilder 建造者类
 * @Author Shanya
 * @Date 2021-3-16
 * @Version 3.0.0
 */
object SerialPortBuilder {
    //获取SerialPort实例
    private val serialPort = SerialPort.get()
    //是否开启自动连接标志
    private var isAutoConnect = false
    /**
     * 是否开启Debug模式（打印日志Logcat）
     * @param status 开启状态
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun isDebug(status: Boolean): SerialPortBuilder {
        serialPort.isDebug(status)
        return this
    }
    /**
     * 是否开启自动连接
     * @param status 开启状态
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun autoConnect(status: Boolean): SerialPortBuilder {
        isAutoConnect = status
        return this
    }

    /**
     * 是否开启在未连接设备发送数据时自动打开默认搜索页面
     * @param status 开启状态
     * @Author Shanya
     * @Date 2021-4-21
     * @Version 3.0.0
     */
    fun autoOpenDiscoveryActivity(status: Boolean): SerialPortBuilder {
        SerialPort.autoOpenDiscoveryActivity = true
        return this
    }

    /**
     * 是否开启十六进制字符串自动转换成字符串
     * @param status 开启状态
     * @Author Shanya
     * @Date 2021-3-24
     * @Version 3.0.0
     */
    fun autoHexStringToString(status: Boolean): SerialPortBuilder {
        SerialPort.hexStringToStringFlag = status
        return this
    }

    /**
     * 设置接收数据格式
     * @param type 接收数据格式（默认字符串格式）
     * 可选参数 READ_STRING（字符串格式） READ_HEX（十六进制格式）
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun setReadDataType(type: Int): SerialPortBuilder {
        SerialPort.readDataType = type
        return this
    }
    /**
     * 设置发送数据格式
     * @param type 发送数据格式（默认字符串格式）
     * 可选参数 SEND_STRING（字符串格式） SEND_HEX（十六进制格式）
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun setSendDataType(type: Int): SerialPortBuilder {
        SerialPort.sendDataType = type
        return this
    }
    /**
     * 连接状态回调接口函数
     * @param connectStatusCallback 连接状态回调接口
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun setConnectStatusCallback(connectStatusCallback: ConnectStatusCallback): SerialPortBuilder {
        SerialPort._setConnectStatusCallback(connectStatusCallback)
        return this
    }
    /**
     * 接收数据回调接口函数
     * @param receivedDataCallback 接收数据回调接口
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun setReceivedDataListener(receivedDataCallback: ReceivedDataCallback): SerialPortBuilder {
        SerialPort._setReceivedDataListener(receivedDataCallback)
        return this
    }
    /**
     * 发送数据函数
     * @param data 待发送数据
     * @Author Shanya
     * @Date 2021-4-15
     * @Version 3.0.0
     */
    fun sendData(data: String) {
        serialPort.sendData(data)
    }

    /**
     * 搜索设备函数
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-4-21
     * @Version 3.0.0
     */
    fun doDiscovery(context: Context) {
        serialPort.doDiscovery(context)
    }
    /**
     * 创建实例
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun build(context: Context): SerialPort {
        if (!SerialPort.bluetoothAdapter.isEnabled) {
            SerialPort.bluetoothAdapter.enable()
        }
        serialPort.build(context)
        if (isAutoConnect) {
            SPUtil.getSPDevice(context)?.let { SerialPort._connectDevice(it) }
            SerialPort.autoConnectFlag = true
        }
        return serialPort
    }
}