package world.shanya.serialport

import android.annotation.SuppressLint
import android.content.Context
import world.shanya.serialport.connect.*
import world.shanya.serialport.connect.SerialPortConnect
import world.shanya.serialport.discovery.DiscoveryStatusCallback
import world.shanya.serialport.discovery.DiscoveryStatusWithTypeCallback
import world.shanya.serialport.tools.SPUtil


/**
 * SerialPortBuilder 建造者类
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
@SuppressLint("MissingPermission")
object SerialPortBuilder {
    //获取SerialPort实例
    @SuppressLint("StaticFieldLeak")
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
     * setLegacyUUID 设置传统设备UUID
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun setLegacyUUID(uuid: String):SerialPortBuilder {
        SerialPortConnect.UUID_LEGACY = uuid
        return this
    }

    /**
     * setBleUUID 设置BLE设备UUID
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun setBleUUID(uuid: String):SerialPortBuilder {
        SerialPortConnect.UUID_BLE = uuid
        return this
    }

    fun setBleReadUUID(uuid: String):SerialPortBuilder {
        SerialPortConnect.UUID_BLE_READ = uuid
        return this
    }

    fun setBleSendUUID(uuid: String):SerialPortBuilder {
        SerialPortConnect.UUID_BLE_SEND = uuid
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
     * 是否开启间隔自动重连
     * @param status 开启状态
     * @param time 间隔时间 单位ms 默认值 10000
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun setAutoReconnectAtIntervals(status: Boolean, time: Int = 10000): SerialPortBuilder {
        SerialPortConnect.autoReconnectAtIntervalsFlag = status
        SerialPortConnect.autoReconnectIntervalsTime = time
        if (status) {
            SerialPortConnect.autoConnect()
        } else {
            SerialPortConnect.cancelAutoConnect()
        }
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
        SerialPort.autoOpenDiscoveryActivityFlag = status
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
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    @Deprecated("该方法在4.0.0版本开始被弃用", ReplaceWith("setConnectionStatusCallback"))
    fun setConnectStatusCallback(connectStatusCallback: ConnectStatusCallback): SerialPortBuilder {
        SerialPort._setConnectStatusCallback(connectStatusCallback)
        return this
    }

    /**
     * 连接状态带类型回调接口函数
     * @param connectionStatusCallback 连接状态带类型回调接口
     * @Author Shanya
     * @Date 2021-8-13
     * @Version 4.0.3
     */
    fun setConnectionStatusCallback(connectionStatusCallback: ConnectionStatusCallback): SerialPortBuilder {
        SerialPort._setConnectionStatusCallback(connectionStatusCallback)
        return this
    }

    /**
     * 搜索状态回调接口函数
     * @param discoveryStatusCallback 搜索状态回调接口
     * @Author Shanya
     * @Date 2021-8-13
     * @Version 4.0.3
     */
    fun setDiscoveryStatusCallback(discoveryStatusCallback: DiscoveryStatusCallback): SerialPortBuilder {
        SerialPort._setDiscoveryStatusListener(discoveryStatusCallback)
        return this
    }

    /**
     * 搜索状态回调接口函数
     * @param discoveryStatusWithTypeCallback 搜索状态回调接口
     * @Author Shanya
     * @Date 2021-8-13
     * @Version 4.0.3
     */
    fun setDiscoveryStatusWithTypeCallback(discoveryStatusWithTypeCallback: DiscoveryStatusWithTypeCallback): SerialPortBuilder {
        SerialPort._setDiscoveryStatusWithTypeListener(discoveryStatusWithTypeCallback)
        return this
    }

    /**
     * 接收数据回调接口函数
     * @param receivedDataCallback 接收数据回调接口
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    @Deprecated(message = "该方法在4.0.2版本开始被弃用",replaceWith = ReplaceWith("setReceivedDataCallback"))
    fun setReceivedDataListener(receivedDataCallback: ReceivedDataCallback): SerialPortBuilder {
        SerialPort._setReceivedDataListener(receivedDataCallback)
        return this
    }

    /**
     * 接收数据回调接口函数
     * @param receivedDataCallback 接收数据回调接口
     * @Author Shanya
     * @Date 2021-8-7
     * @Version 4.0.2
     */
    fun setReceivedDataCallback(receivedDataCallback: ReceivedDataCallback): SerialPortBuilder {
        SerialPort._setReceivedDataListener(receivedDataCallback)
        return this
    }

    /**
     * 接收字节数组回调接口函数
     * @param receivedBytesCallback 收到字节数组回调接口
     * @Author Shanya
     * @Date 2021-12-10
     * @Version 4.1.2
     */
    fun setReceivedBytesCallback(receivedBytesCallback: ReceivedBytesCallback): SerialPortBuilder {
        SerialPort._setReceivedBytesListener(receivedBytesCallback)
        return this
    }

    /**
     * 连接结果回调接口函数
     * @param connectionResultCallback 连接结果回调接口
     * @Author Shanya
     * @Date 2021-9-14
     * @Version 4.1.1
     */
    @Deprecated(message = "该方法在4.2.0版本开始被弃用",replaceWith = ReplaceWith("setConnectionStatusCallback"))
    fun setConnectionResultCallback(connectionResultCallback: ConnectionResultCallback): SerialPortBuilder {
        SerialPort._setConnectionResultCallback(connectionResultCallback)
        return this
    }

    fun setBleCanWorkCallback(bleCanWorkCallback: BleCanWorkCallback): SerialPortBuilder {
        SerialPort._setBleCanWorkCallback(bleCanWorkCallback)
        return this
    }

    /**
     * 发送数据函数
     * @param data 待发送数据
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun sendData(data: String) {
        serialPort.sendData(data)
    }

    fun sendData(bytes: ByteArray) {
        serialPort.sendData(bytes)
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
     * 是否忽略没有名字的蓝牙设备
     * @param status
     * @Author Shanya
     * @Date 2021/5/28
     * @Version 3.1.0
     */
    fun isIgnoreNoNameDevice(status: Boolean): SerialPortBuilder {
        SerialPort.isIgnoreNoNameDevice(status)
        return this
    }


    /**
     * 默认连接页面连接方式选择对话框标志位
     * @param status 开启状态
     * @Author Shanya
     * @Date 2022-1-12
     * @Version 4.1.4
     */
    fun setOpenConnectionTypeDialogFlag(status: Boolean):SerialPortBuilder {
        SerialPort.openConnectionTypeDialogFlag = status
        return this
    }

    /**
     * 设置配置信息
     * @param config 配置器
     * @Author Shanya
     * @Date 2022-2-12
     * @Version 4.1.5
     */
    fun setConfig(config: SerialPortConfig):SerialPortBuilder {
        isDebug(config.debug)
        setLegacyUUID(config.UUID_LEGACY)
        setBleUUID(config.UUID_BLE)
        setBleReadUUID(config.UUID_BLE_READ)
        setBleSendUUID(config.UUID_BLE_SEND)
        autoConnect(config.autoConnect)
        setAutoReconnectAtIntervals(config.autoReconnect, config.reconnectAtIntervals)
        autoOpenDiscoveryActivity(config.autoOpenDiscoveryActivity)
        autoHexStringToString(config.autoHexStringToString)
        setReadDataType(config.readDataType)
        setSendDataType(config.sendDataType)
        isIgnoreNoNameDevice(config.ignoreNoNameDevice)
        setOpenConnectionTypeDialogFlag(config.openConnectionTypeDialogFlag)

        return this
    }

    fun setDiscoveryTime(time: Long): SerialPortBuilder {
        serialPort.setDiscoveryTime(time)
        return this
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
        SPUtil.getDeviceType(context)?.let {type ->
            when (type) {
                "1" -> {
                    SPUtil.getDeviceAddress(context)?.let {
                        SerialPortConnect.lastDeviceAddress = it
                        if (isAutoConnect) {
                            SerialPortConnect._connectLegacy(context, it)
                            SerialPortConnect.autoConnectFlag = true
                        }
                    }
                }
                "2" -> {
                    SPUtil.getDeviceAddress(context)?.let {
                        SerialPortConnect.lastDeviceAddress = it
                        if (isAutoConnect) {
                            SerialPortConnect.connectBle(context, it)
                            SerialPortConnect.autoConnectFlag = true
                        }
                    }
                }
                else -> {

                }
            }
        }
        return serialPort
    }

    fun printPossibleBleUUID() {
        serialPort.printPossibleBleUUID()
    }
}