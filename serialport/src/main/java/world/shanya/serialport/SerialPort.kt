package world.shanya.serialport

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.connect.*
import world.shanya.serialport.connect.SerialPortConnect
import world.shanya.serialport.discovery.*
import world.shanya.serialport.discovery.SerialPortDiscovery
import world.shanya.serialport.service.SerialPortService
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.tools.*
import world.shanya.serialport.tools.HexStringToString
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.ToastUtil
import java.util.*


//接收消息接口
typealias ReceivedDataCallback = (data: String) -> Unit
typealias ReceivedBytesCallback = (bytes: ByteArray) -> Unit

/**
 * SerialPort API
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
@SuppressLint("StaticFieldLeak", "MissingPermission")
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

        //连接方式选择对话框标志位
        internal var openConnectionTypeDialogFlag = false

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
        //BLE发送分包间隔延时
        var bleSendSleep = 10

        internal var discoveryTimeOut = false
        internal var discoveryTime:Long = 12000

        /**
         * setLegacyUUID 设置传统设备UUID
         * @Author Shanya
         * @Date 2021-7-21
         * @Version 4.0.0
         */
        fun setLegacyUUID(uuid: String) {
            SerialPortConnect.UUID_LEGACY = uuid
            LogUtil.log("设置传统设备UUID", uuid)
        }

        /**
         * setBleUUID 设置BLE设备UUID
         * @Author Shanya
         * @Date 2021-7-21
         * @Version 4.0.0
         */
        fun setBleUUID(uuid: String) {
            SerialPortConnect.UUID_BLE = uuid
            LogUtil.log("设置BLE设备UUID", uuid)
        }

        fun setBleReadUUID(uuid: String) {
            SerialPortConnect.UUID_BLE_READ = uuid
            LogUtil.log("设置BLE设备接收UUID", uuid)
        }

        fun setBleSendUUID(uuid: String) {
            SerialPortConnect.UUID_BLE_SEND = uuid
            LogUtil.log("设置BLE设备发送UUID", uuid)
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

        //收到字节数组回调接口
        internal var receivedBytesCallback: ReceivedBytesCallback ?= null
        /**
         * 内部静态接收字节数组回调接口函数
         * @param receivedBytesCallback 收到字节数组回调接口
         * @Author Shanya
         * @Date 2021-12-10
         * @Version 4.1.2
         */
        internal fun _setReceivedBytesListener(receivedBytesCallback: ReceivedBytesCallback) {
            this.receivedBytesCallback = receivedBytesCallback
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

        internal var bleCanWorkCallback: BleCanWorkCallback? = null

        internal fun _setBleCanWorkCallback(bleCanWorkCallback: BleCanWorkCallback) {
            this.bleCanWorkCallback = bleCanWorkCallback
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

        //连接结果回调
        @Deprecated(message = "该方法在4.2.0版本开始被弃用",replaceWith = ReplaceWith("connectionStatusCallback"))
        internal var connectionResultCallback: ConnectionResultCallback ?= null

        /**
         * 内连接结果回调接口函数
         * @param connectionResultCallback 连接结果回调接口
         * @Author Shanya
         * @Date 2021-9-14
         * @Version 4.1.1
         */
        @Deprecated(message = "该方法在4.2.0版本开始被弃用",replaceWith = ReplaceWith("_setConnectionStatusCallback"))
        internal fun _setConnectionResultCallback(connectionResultCallback: ConnectionResultCallback) {
            this.connectionResultCallback =connectionResultCallback
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
                SerialPortConnect.connectLegacy(it, device.address)
            }
        }

        internal fun _connectBleDevice(device: BluetoothDevice) {
            newContext?.let {
                SerialPortConnect.connectBle(it, device.address)
            }
        }

        /**
         * 连接函数
         * @param device 连接设备
         * @Author Shanya
         * @Date 2021-7-21
         * @Version 4.0.0
         */
        internal fun _connectDevice(device: BluetoothDevice, context: Context) {
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
                SerialPortTools.string2bytes(data, "GBK")
            }else{
                DataUtil.string2hex(data)?.toList()!!.toByteArray()
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
        when (type) {
            READ_HEX -> {
                LogUtil.log("设置接收数据格式", "十六进制")
            }
            READ_STRING -> {
                LogUtil.log("设置接收数据格式", "字符串")
            }
            else -> {
                LogUtil.log("未知格式，需详查！")
            }
        }
    }


    /**
     * 设置发送数据格式
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun setSendDataType(type: Int) {
        sendDataType = type
        when (type) {
            SEND_HEX -> {
                LogUtil.log("设置发送数据格式", "十六进制")
            }
            SEND_STRING -> {
                LogUtil.log("设置发送数据格式", "字符串")
            }
            else -> {
                LogUtil.log("未知格式，需详查！")
            }
        }
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
     * 接收字节数组回调接口函数
     * @param receivedBytesCallback 收到字节数组回调接口
     * @Author Shanya
     * @Date 2021-12-10
     * @Version 4.1.2
     */
    fun setReceivedBytesCallback(receivedBytesCallback: ReceivedBytesCallback) {
        _setReceivedBytesListener(receivedBytesCallback)
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

    fun setBleCanWorkCallback(bleCanWorkCallback: BleCanWorkCallback /* = (status: kotlin.Boolean) -> kotlin.Unit */) {
        _setBleCanWorkCallback(bleCanWorkCallback)
    }

    /**
     * 连接结果回调接口函数
     * @param connectionResultCallback 连接结果回调接口
     * @Author Shanya
     * @Date 2021-9-14
     * @Version 4.1.1
     */
    @Deprecated(message = "该方法在4.2.0版本开始被弃用",replaceWith = ReplaceWith("setConnectionStatusCallback"))
    fun setConnectionResultCallback(connectionResultCallback: ConnectionResultCallback) {
        _setConnectionResultCallback(connectionResultCallback)
    }

    /**
     * 打印可能的BLE设备UUID
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun printPossibleBleUUID() {
        if (SerialPortConnect.gattCharacteristicList.size == 0) {
            LogUtil.log("请先连接BLE设备之后，再执行此函数！")
            return
        }
        for (gattService in SerialPortConnect.gattServiceList) {
            LogUtil.log("Service", gattService.key)
            for (gattCharacteristic in gattService.value) {
                LogUtil.log("   Characteristic", gattCharacteristic.key)
                LogUtil.log("   Properties", gattCharacteristic.value.toString(2))
            }
        }
        LogUtil.log("Properties 具体含义请查询官网, https://serialportsample.readthedocs.io/zh_CN/latest/tutorials/tools_java.html#uuid")
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
        SerialPortTools.bleSendData(SerialPortConnect.bluetoothGatt,SerialPortConnect.sendGattCharacteristic,data)
    }

    private fun sendBleData(bytes: ByteArray) {
        SerialPortTools.bleSendData(
            SerialPortConnect.bluetoothGatt,
            SerialPortConnect.sendGattCharacteristic, bytes)
    }

    fun sendData(bytes: ByteArray) {
        if (SerialPortConnect.connectStatus) {
            SerialPortConnect.connectedBleDevice?.let {
                sendBleData(bytes)
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
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            //判断是否以授权相机权限，没有则授权
            LogUtil.log("请先获取定位权限！")
            ToastUtil.toast(context, SerialPortToast.permission)
        } else {
            SerialPortDiscovery.startLegacyScan(context)
            SerialPortDiscovery.startBleScan()
            discoveryTimeOut = false
            Timer().schedule(object:TimerTask(){
                override fun run() {
                    discoveryTimeOut = true
                    MainScope().launch {
                        newContext?.let {
                            cancelDiscovery(it)
                        }
                        SerialPortDiscovery.discoveryStatusWithTypeCallback?.invoke(DISCOVERY_LEGACY, false)
                        SerialPortDiscovery.discoveryStatusCallback?.invoke(false)
                        SerialPortDiscovery.discoveryStatusLiveData.value = false
                    }
                }
            }, discoveryTime)
        }

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
        discoveryTimeOut = true
    }

    /**
     * setBleUUID 设置Ble设备UUID
     * @Author Shanya
     * @Date 2022-1-12
     * @Version 4.1.4
     */
    fun setBleUUID(uuid: String) {
        SerialPortConnect.UUID_BLE = uuid
        LogUtil.log("设置BLE设备UUID", uuid)
    }

    fun setBleReadUUID(uuid: String) {
        SerialPortConnect.UUID_BLE_READ = uuid
        LogUtil.log("设置BLE设备接收UUID", uuid)
    }

    fun setBleSendUUID(uuid: String) {
        SerialPortConnect.UUID_BLE_SEND = uuid
        LogUtil.log("设置BLE设备发送UUID", uuid)
    }

    /**
     * setLegacyUUID 设置传统设备UUID
     * @Author Shanya
     * @Date 2022-1-12
     * @Version 4.1.4
     */
    fun setLegacyUUID(uuid: String) {
        SerialPortConnect.UUID_LEGACY = uuid
        LogUtil.log("设置传统设备UUID", uuid)
    }

    /**
     * 默认连接页面连接方式选择对话框标志位
     * @param status 开启状态
     * @Author Shanya
     * @Date 2022-1-12
     * @Version 4.1.4
     */
    fun setOpenConnectionTypeDialogFlag(status: Boolean) {
        openConnectionTypeDialogFlag = status
    }

    fun requestMtu(mtu: Int):Boolean {
        return SerialPortConnect.requestMtu(mtu)
    }

    fun setDiscoveryTime(time: Long) {
        discoveryTime = time
    }

}