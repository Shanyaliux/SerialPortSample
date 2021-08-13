package world.shanya.serialport

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import world.shanya.serialport.connect.*
import world.shanya.serialport.connect.SerialPortConnect
import world.shanya.serialport.discovery.*
import world.shanya.serialport.discovery.SerialPortDiscovery
import world.shanya.serialport.service.SerialPortService
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.tools.*
import world.shanya.serialport.tools.HexStringToString
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.StringToHex
import world.shanya.serialport.tools.ToastUtil


//接收消息接口
typealias ReceivedDataCallback = (data: String) -> Unit

/**
 * SerialPort API
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
@SuppressLint("StaticFieldLeak")
class SerialPort private constructor() {
    companion object {

        /***** Singleton  *****/
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
        /**********************/


        //修改Toast提示的引用
        val serialPortToast = SerialPortToast.get()

        /**
         * 接收发送数据格式标签
         */
        //接收数据格式为字符串
        const val READ_STRING = 1
        //发送数据格式为字符串
        const val SEND_STRING = 2
        //接收数据格式为十六进制
        const val READ_HEX = 3
        //发送数据格式为十六进制
        const val SEND_HEX = 4

        const val DISCOVERY_BLE = 0
        const val DISCOVERY_LEGACY = 1


        //传统设备搜索结果广播接收器
        internal val discoveryBroadcastReceiver = DiscoveryBroadcastReceiver()
        //传统设备连接状态变更广播接收器
        internal val bluetoothStatusBroadcastReceiver = BluetoothStatusBroadcastReceiver()
        //接收数据格式默认为字符串
        internal var readDataType = READ_STRING
        //发送数据格式默认为字符串
        internal var sendDataType = SEND_STRING
        //蓝牙适配器
        internal val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //新的上下文
        internal var newContext: Context ?= null
        //旧的上下文
        private var oldContext: Context ?= null
        //十六进制字符串转换成字符串标志
        internal var hexStringToStringFlag = false
        //自动打开搜索页面标志
        internal var autoOpenDiscoveryActivityFlag = false
        //是否在搜索是忽略没有名字的蓝牙设备
        internal var ignoreNoNameDeviceFlag = false


        /**
         * setLegacyUUID 设置传统设备UUID
         * @Author Shanya
         * @Date 2021-7-21
         * @Version 4.0.0
         */
        fun setLegacyUUID(uuid: String) {
            SerialPortConnect.UUID_LEGACY = uuid
        }

        /**
         * setBleUUID 设置BLE设备UUID
         * @Author Shanya
         * @Date 2021-7-21
         * @Version 4.0.0
         */
        fun setBleUUID(uuid: String) {
            SerialPortConnect.UUID_BLE = uuid
        }

        /**
        * 是否忽略没有名字的蓝牙设备
        * @param status
        * @Author Shanya
        * @Date 2021-7-21
        * @Version 4.0.0
        */
        fun isIgnoreNoNameDevice(status: Boolean) {
            ignoreNoNameDeviceFlag = status
        }

        //找到设备回调接口
        internal var findUnpairedDeviceCallback: FindUnpairedDeviceCallback ?= null
        /**
         * 发现设备回调接口函数
         * @param findUnpairedDeviceCallback 找到设备回调接口
         * @Author Shanya
         * @Date 2021-3-16
         * @Version 3.0.0
         */
        internal fun setFindDeviceListener(findUnpairedDeviceCallback: FindUnpairedDeviceCallback) {
            this.findUnpairedDeviceCallback = findUnpairedDeviceCallback
        }

        //收到消息回调接口
        internal var receivedDataCallback: ReceivedDataCallback ?= null
        /**
         * 内部静态接收消息回调接口函数
         * @param receivedDataCallback 收到消息回调接口
         * @Author Shanya
         * @Date 2021-3-16
         * @Version 3.0.0
         */
        internal fun _setReceivedDataListener(receivedDataCallback: ReceivedDataCallback) {
            this.receivedDataCallback = receivedDataCallback
        }

        /**
         * 内部静态搜索状态带类型回调接口函数
         * @param discoveryStatusWithTypeCallback 搜索状态带类型回调接口
         * @Author Shanya
         * @Date 2021-8-13
         * @Version 4.0.3
         */
        internal fun _setDiscoveryStatusWithTypeListener(discoveryStatusWithTypeCallback: DiscoveryStatusWithTypeCallback) {
            SerialPortDiscovery.discoveryStatusWithTypeCallback = discoveryStatusWithTypeCallback
        }

        /**
         * 内部静态搜索状态回调接口函数
         * @param discoveryStatusCallback 搜索状态回调接口
         * @Author Shanya
         * @Date 2021-8-7
         * @Version 4.0.2
         */
        internal fun _setDiscoveryStatusListener(discoveryStatusCallback: DiscoveryStatusCallback) {
            SerialPortDiscovery.discoveryStatusCallback = discoveryStatusCallback
        }

        //内部连接回调，不包含连接信息（成功与否和连接设备）
        internal var connectCallback: ConnectCallback ?= null
        /**
         * 内部连接回调接口函数
         * @param connectCallback 内部连接回调接口
         * @Author Shanya
         * @Date 2021-3-16
         * @Version 3.0.0
         */
        internal fun setConnectListener(connectCallback: ConnectCallback) {
            this.connectCallback = connectCallback
        }

        //连接状态回调，外部使用（包含成功与否和连接设备）
        @Deprecated(message = "该回调变量在版本4.0.0开始被弃用",
            replaceWith = ReplaceWith(expression = "connectionStatusCallback"))
        internal var connectStatusCallback: ConnectStatusCallback ?= null

        //连接状态回调，外部使用（包含成功与否和连接设备）
        internal var connectionStatusCallback:ConnectionStatusCallback ?= null

        /**
         * 内连接状态回调接口函数
         * @param connectStatusCallback 连接状态回调接口
         * @Author Shanya
         * @Date 2021-3-16
         * @Version 3.0.0
         */
        @Deprecated(message = "该方法在版本4.0.0开始被弃用",
            replaceWith = ReplaceWith(expression = "_setConnectionStatusCallback"))
        internal fun _setConnectStatusCallback(connectStatusCallback: ConnectStatusCallback) {
            this.connectStatusCallback = connectStatusCallback
        }

        /**
         * 内连接状态回调接口函数
         * @param connectionStatusCallback 连接状态回调接口
         * @Author Shanya
         * @Date 2021-7-21
         * @Version 4.0.0
         */
        internal fun _setConnectionStatusCallback(connectionStatusCallback: ConnectionStatusCallback) {
            this.connectionStatusCallback = connectionStatusCallback
        }


        /**
         * 十六进制字符串转换成字符串
         * @param hexString 待转换十六进制字符串
         * @return 转换完成的字符串
         * @Author Shanya
         * @Date 2021-3-16
         * @Version 3.0.0
         */
        internal fun _hexStringToString(hexString: String): String? {
            return HexStringToString.conversion(hexString)
        }

        /**
         * 内部使用传统设备连接函数
         * @param device 连接设备
         * @Author Shanya
         * @Date 2021-7-21
         * @Version 4.0.0
         */
        internal fun _connectLegacyDevice(device: BluetoothDevice) {
            newContext?.let {
                SerialPortConnect.connectLegacy(it,device.address)
            }
        }

        /**
         * 连接函数
         * @param device 连接设备
         * @Author Shanya
         * @Date 2021-7-21
         * @Version 4.0.0
         */
        internal fun _connectDevice(device: BluetoothDevice) {
            newContext?.let {
                if (device.type == 2) {
                    SerialPortConnect.connectBle(it, device.address)
                } else {
                    SerialPortConnect.connectLegacy(it, device.address)
                }
            }
        }

        /**
         * 内部使用传统设备断开连接函数
         * @Author Shanya
         * @Date 2021-7-21
         * @Version 4.0.0
         */
        internal fun _legacyDisconnect() {
            newContext?.let {
                it.stopService(Intent(it, SerialPortService::class.java))
            }
        }
    }

    /**
     * 类初始化
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    init {
        SerialPortDiscovery.discoveryStatusLiveData.value = false
        if (!bluetoothAdapter.isEnabled) {
            val res = bluetoothAdapter.enable()
            if (res) {
                LogUtil.log("蓝牙打开成功")
                newContext?.let {
                    ToastUtil.toast(it, SerialPortToast.openBluetoothSucceeded)
                }
            } else {
                LogUtil.log("蓝牙打开失败")
                newContext?.let {
                    ToastUtil.toast(it, SerialPortToast.openBluetoothFailed)
                }
            }
        }
    }

    /**
     * 是否开启Debug模式（Debug模式会打印日志）
     * @param status 开启状态
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    internal fun isDebug(status: Boolean):SerialPort {
        LogUtil.status = status
        return this
    }

    /**
     * 创建实例，获取上下文并注册相应广播
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    internal fun build(context: Context) {
        oldContext?.unregisterReceiver(bluetoothStatusBroadcastReceiver)
        oldContext = newContext
        newContext = context
        val intentFilterConnection = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        val intentFilterBluetoothState = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilterConnection.priority = Int.MAX_VALUE
        intentFilterBluetoothState.priority = Int.MAX_VALUE
        context.registerReceiver(bluetoothStatusBroadcastReceiver, intentFilterConnection)
        context.registerReceiver(bluetoothStatusBroadcastReceiver, intentFilterBluetoothState)
    }

    /**
     * 内部发送数据函数（异步线程）
     * @param data 待发送数据
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    private fun send(data : String){
        try {
            val outputStream = SerialPortConnect.bluetoothSocket?.outputStream
            val bos: ByteArray = if (sendDataType == SEND_STRING) {
                data.toByteArray()
            }else{
                StringToHex.stringToHex(data)?.toList()!!.toByteArray()
            }
            outputStream?.write(bos)
            LogUtil.log("SerialPort","发送数据: $data")
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    /**
     * 打开搜索页面
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun openDiscoveryActivity() {
        val intent = Intent(newContext,DiscoveryActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        newContext?.startActivity(Intent(newContext,DiscoveryActivity::class.java))
    }

    /**
     * 打开搜索页面
     * @param intent
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun openDiscoveryActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        newContext?.startActivity(intent)
    }

    /**
     * 断开连接
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun disconnect() {
        SerialPortConnect.connectedBleDevice?.let {
            SerialPortConnect.bleDisconnect()
        }
        SerialPortConnect.connectedLegacyDevice?.let {
            newContext?.let { context ->
                SerialPortConnect.legacyDisconnect(context)
            }
        }



    }

    /**
     * 设置接收数据格式
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun setReadDataType(type: Int) {
        readDataType = type
    }


    /**
     * 设置发送数据格式
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun setSendDataType(type: Int) {
        sendDataType = type
    }

    /**
     * 连接传统设备
     * @param address 设备地址
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun connectLegacyDevice(address: String) {
        val device = bluetoothAdapter.getRemoteDevice(address)
        _connectLegacyDevice(device)
    }

    /**
     * 连接函数
     * @param device 连接设备
     * @Author Shanya
     * @Date 2021-8-13
     * @Version 4.0.3
     */
    fun connectDevice(address: String) {
        newContext?.let {
            val device = bluetoothAdapter.getRemoteDevice(address)
            if (device.type == BluetoothDevice.DEVICE_TYPE_LE) {
                SerialPortConnect.connectBle(it, device.address)
            } else {
                SerialPortConnect.connectLegacy(it, device.address)
            }
        }
    }

    /**
     * 连接BLE设备
     * @param address 设备地址
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun connectBle(address: String) {
        newContext?.let {
            SerialPortConnect.connectBle(it,address)
        }
    }

    /**
     * 接收数据回调接口函数
     * @param receivedDataCallback 接收数据回调接口
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    @Deprecated(message = "该方法在4.0.2版本开始被弃用",replaceWith = ReplaceWith("setReceivedDataCallback"))
    fun setReceivedDataListener(receivedDataCallback: ReceivedDataCallback) {
        _setReceivedDataListener(receivedDataCallback)
    }

    /**
     * 接收数据回调接口函数
     * @param receivedDataCallback 接收数据回调接口
     * @Author Shanya
     * @Date 2021-8-7
     * @Version 4.0.2
     */
    fun setReceivedDataCallback(receivedDataCallback: ReceivedDataCallback) {
        _setReceivedDataListener(receivedDataCallback)
    }

    /**
     * 内部静态搜索状态带类型回调接口函数
     * @param discoveryStatusWithTypeCallback 搜索状态回调接口
     * @Author Shanya
     * @Date 2021-8-13
     * @Version 4.0.3
     */
    fun setDiscoveryStatusWithTypeCallback(discoveryStatusWithTypeCallback: DiscoveryStatusWithTypeCallback) {
        _setDiscoveryStatusWithTypeListener(discoveryStatusWithTypeCallback)
    }

    /**
     * 内部静态搜索状态回调接口函数
     * @param discoveryStatusCallback 搜索状态回调接口
     * @Author Shanya
     * @Date 2021-8-7
     * @Version 4.0.2
     */
    fun setDiscoveryStatusCallback(discoveryStatusCallback: DiscoveryStatusCallback) {
        _setDiscoveryStatusListener(discoveryStatusCallback)
    }

    /**
     * 连接状态回调接口函数
     * @param connectStatusCallback 连接状态回调接口
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    @Deprecated(message = "该方法在4.0.0版本开始被弃用",replaceWith = ReplaceWith("setConnectionStatusCallback"))
    fun setConnectStatusCallback(connectStatusCallback: ConnectStatusCallback) {
        _setConnectStatusCallback(connectStatusCallback)
    }

    /**
     * 连接状态回调接口函数
     * @param connectionStatusCallback 连接状态回调接口
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun setConnectionStatusCallback(connectionStatusCallback: ConnectionStatusCallback) {
        _setConnectionStatusCallback(connectionStatusCallback)
    }

    /**
     * 打印可能的BLE设备UUID
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun printPossibleBleUUID() {
        if (SerialPortConnect.bleUUIDList.size == 0) {
            LogUtil.log("请先连接BLE设备之后，再执行此函数！")
            return
        }
        for (uuid in SerialPortConnect.bleUUIDList) {
            LogUtil.log("PossibleBleUUID",uuid)
        }
    }

    /**
     * 传统设备发送数据
     * @param data 待发送数据
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    private fun sendLegacyData(data:String){
        Thread{
            send(data)
        }.start()
    }

    /**
     * Ble设备发送数据
     * @param data 待发送数据
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    private fun sendBleData(data: String) {
        SerialPortToolsByJava.bleSendData(SerialPortConnect.bluetoothGatt,SerialPortConnect.gattCharacteristic,data)
    }

    /**
     * 发送数据
     * @param data 待发送数据
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun sendData(data: String) {

        if (SerialPortConnect.connectStatus) {
            SerialPortConnect.connectedLegacyDevice?.let {
                sendLegacyData(data)
            }
            SerialPortConnect.connectedBleDevice?.let {
                sendBleData(data)
            }
        } else {
            LogUtil.log("请先连接设备，再发送数据")
            newContext?.let {context ->
                ToastUtil.toast(context,SerialPortToast.connectFirst)
            }
            if (autoOpenDiscoveryActivityFlag) {
                openDiscoveryActivity()
            }
        }
    }

    /**
     * 十六进制字符串转换成字符串
     * @param hexString 待转换十六进制字符串
     * @return 转换完成的字符串
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun hexStringToString(hexString: String): String? {
        return HexStringToString.conversion(hexString)
    }

    /**
     * 获取已配对设备列表
     * @return 已配对设备列表
     * @Author Shanya
     * @Date 2021-3-26
     * @Version 3.0.0
     */
    @Deprecated(message = "建议使用 getPairedDevicesListBD",
    replaceWith = ReplaceWith(
        expression = "getPairedDevicesListBD()"))
    fun getPairedDevicesList() = SerialPortDiscovery.pairedDevicesList

    /**
     * 获取未配对设备列表
     * @return 未配对设备列表
     * @Author Shanya
     * @Date 2021-3-26
     * @Version 3.0.0
     */
    @Deprecated(message = "建议使用 getUnPairedDevicesListBD",
        replaceWith = ReplaceWith(
            expression = "getUnPairedDevicesListBD()"))
    fun getUnPairedDevicesList() = SerialPortDiscovery.unPairedDevicesList

    /**
     * 获取已配对设备列表
     * @return 已配对设备列表
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun getPairedDevicesListBD() = SerialPortDiscovery.pairedDevicesListBD

    /**
     * 获取未配对设备列表
     * @return 未配对设备列表
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun getUnPairedDevicesListBD() = SerialPortDiscovery.unPairedDevicesListBD

    /**
     * 开始搜索
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun doDiscovery(context: Context) {
        SerialPortDiscovery.startLegacyScan(context)
        SerialPortDiscovery.startBleScan()
    }

    /**
     * 停止搜索
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun cancelDiscovery(context: Context) {
        SerialPortDiscovery.stopLegacyScan(context)
        SerialPortDiscovery.stopBleScan()
    }
}