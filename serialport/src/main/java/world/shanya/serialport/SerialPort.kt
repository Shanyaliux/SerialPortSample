package world.shanya.serialport

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.connect.ConnectionBroadcastReceiver
import world.shanya.serialport.discovery.DiscoveryBroadcastReceiver
import world.shanya.serialport.connect.SerialPortConnect
import world.shanya.serialport.discovery.Device
import world.shanya.serialport.discovery.DiscoveryActivity
import world.shanya.serialport.discovery.SerialPortDiscovery
import world.shanya.serialport.service.SerialPortService
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.tools.HexStringToString
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.ToastUtil
import java.io.IOException
import java.io.InputStream
import kotlin.collections.ArrayList


//找到设备接口
typealias FindUnpairedDeviceCallback = () -> Unit
//连接状态接口
typealias ConnectStatusCallback = (status: Boolean, device: BluetoothDevice?) -> Unit
//连接接口
typealias ConnectCallback = () -> Unit
//接收消息接口
typealias ReceivedDataCallback = (data: String) -> Unit

/**
 * SerialPort API
 * @Author Shanya
 * @Date 2021-3-16
 * @Version 3.0.0
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


        //已配对设备列表
        internal val pairedDevicesListBD = ArrayList<BluetoothDevice>()
        //未配对设备列表
        internal val unPairedDevicesListBD = ArrayList<BluetoothDevice>()


        internal val pairedDevicesList = ArrayList<Device>()
        internal val unPairedDevicesList = ArrayList<Device>()

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

        //搜索结果广播接收器
        internal val discoveryBroadcastReceiver = DiscoveryBroadcastReceiver()
        internal val connectionBroadcastReceiver = ConnectionBroadcastReceiver()
        //打印日志工具
        internal val logUtil = LogUtil("SerialPortDebug")
        //接收数据格式默认为字符串
        internal var readDataType = READ_STRING
        //发送数据格式默认为字符串
        internal var sendDataType = SEND_STRING
        //蓝牙适配器
        internal val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //蓝牙通信Socket
        internal var bluetoothSocket: BluetoothSocket?= null
        //蓝牙通信inputStream
        internal var inputStream: InputStream?= null
        //Android蓝牙串口通信UUID
        internal var UUID = "00001101-0000-1000-8000-00805F9B34FB"
        internal var UUID_BLE = "0000ffe1-0000-1000-8000-00805f9b34fb"
        //搜索状态LiveData
        internal var discoveryStatusLiveData = MutableLiveData<Boolean>()
        //新的上下文
        internal var newContext: Context ?= null
        //旧的上下文
        private var oldContext: Context ?= null
        //连接状态
        internal var connectStatus = false
        //自动连接标志（执行自动连接后即为 true）
        internal var autoConnectFlag = false
        //已经连接的设备
        internal var connectedDevice: BluetoothDevice ?= null
        //十六进制字符串转换成字符串标志
        internal var hexStringToStringFlag = false
        //自动打开搜索页面标志
        internal var autoOpenDiscoveryActivityFlag = false
        //没有名字的蓝牙模块忽略
        internal var ignoreNoNameDeviceFlag = false


        /**
         * setUUID 设置UUID
         * @Author Shanya
         * @Date 2021-5-12
         * @Version 3.1.0
         */
        fun setUUID(uuid: String) {
            UUID = uuid
        }

        /**
        * 是否忽略没有名字的蓝牙设备
        * @param status
        * @Author Shanya
        * @Date 2021/5/28
        * @Version 3.1.0
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
        internal var connectStatusCallback: ConnectStatusCallback ?= null
        /**
         * 内连接状态回调接口函数
         * @param connectStatusCallback 连接状态回调接口
         * @Author Shanya
         * @Date 2021-3-16
         * @Version 3.0.0
         */
        internal fun _setConnectStatusCallback(connectStatusCallback: ConnectStatusCallback) {
            this.connectStatusCallback = connectStatusCallback
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
         * 内连接函数
         * @param device 连接设备
         * @Author Shanya
         * @Date 2021-3-16
         * @Version 3.0.0
         */
        internal fun _connectDevice(device: BluetoothDevice) {
            newContext?.let {
                SerialPortConnect.connectLegacy(it,device.address)
            }
        }

        fun connectDevice(device: BluetoothDevice) {
            newContext?.let {
                if (device.type == 2) {
                    SerialPortConnect.connectBle(it, device.address)
                } else {
                    SerialPortConnect.connectLegacy(it, device.address)
                }
            }
        }

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
        discoveryStatusLiveData.value = false
        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
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
        logUtil.status = status
        return this
    }

    /**
     * 创建实例，获取上下文并注册相应广播
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    internal fun build(context: Context) {
        oldContext?.unregisterReceiver(connectionBroadcastReceiver)
        oldContext = newContext
        newContext = context
        val intentFilter = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        intentFilter.priority = Int.MAX_VALUE
        context.registerReceiver(connectionBroadcastReceiver, intentFilter)
    }

    /**
     * 字符串转换成十六进制
     * @param str 待转换的字符串
     * @return 十六进制数组
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    private fun stringToHex(str: String): ArrayList<Byte>? {
        val chars = "0123456789ABCDEF".toCharArray()
        val stingTemp = str.replace(" ","")
        val bs = stingTemp.toCharArray()
        var bit = 0
        var i = 0
        val intArray = ArrayList<Byte>()
        if (stingTemp.length and 0x01 != 0){
            MainScope().launch {
                newContext?.let {
                    ToastUtil.toast(it, SerialPortToast.hexTip)
                }
            }
            throw  RuntimeException("字符个数不是偶数")
        }
        while (i < bs.size) {
            for (j in chars.indices) {
                if (bs[i] == chars[j]) {
                    bit += (j * 16)
                }
                if (bs[i + 1] == chars[j]) {
                    bit += j
                }
            }
            intArray.add(bit.toByte())
            i += 2
            bit = 0
        }
        return intArray
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
            val outputStream = bluetoothSocket?.outputStream
            var n = 0
            val bos: ByteArray = if (sendDataType == SEND_STRING) {
                data.toByteArray()
            }else{
                stringToHex(data)?.toList()!!.toByteArray()
            }

            for (bo in bos) {
                if (bo.toInt() == 0x0a) {
                    n++
                }
            }
            val bosNew = ByteArray(bos.size + n)
            n = 0
            for (bo in bos) {
                if (bo.toInt() == 0x0a) {
                    bosNew[n++] = 0x0d
                    bosNew[n] = 0x0a
                } else {
                    bosNew[n] = bo
                }
                n++
            }
            outputStream?.write(bosNew)
            logUtil.log("SerialPort","发送数据: $data")
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    /**
     * 打开搜索页面
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun openDiscoveryActivity() {
        newContext?.startActivity(Intent(newContext,DiscoveryActivity::class.java))
    }

    /**
     * 打开搜索页面
     * @param intent
     * @Author Shanya
     * @Date 2021-3-26
     * @Version 3.0.0
     */
    fun openDiscoveryActivity(intent: Intent) {
        newContext?.startActivity(intent)
    }

    /**
     * 断开连接
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun disconnect() {
//        try {
//            connectStatus = false
//            bluetoothSocket?.close()
//            newContext?.stopService(Intent(newContext,SerialPortService::class.java))
//
//        }catch (e: IOException){
//            e.printStackTrace()
//        }
        SerialPortConnect.bleDisconnect()
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
     * 连接设备
     * @param address 设备地址
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun connectDevice(address: String) {
        val device = bluetoothAdapter.getRemoteDevice(address)
        _connectDevice(device)
    }

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
    fun setReceivedDataListener(receivedDataCallback: ReceivedDataCallback) {
        _setReceivedDataListener(receivedDataCallback)
    }

    /**
     * 连接状态回调接口函数
     * @param connectStatusCallback 连接状态回调接口
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun setConnectStatusCallback(connectStatusCallback: ConnectStatusCallback) {
        _setConnectStatusCallback(connectStatusCallback)
    }

    /**
     * 发送数据
     * @param data 待发送数据
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun sendData(data:String){
        if (!bluetoothAdapter.isEnabled){
            bluetoothAdapter.enable()
            return
        }

        if (bluetoothSocket == null) {
            MainScope().launch {
                newContext?.let {context ->
                    ToastUtil.toast(context,SerialPortToast.connectFirst)
                }
                if (autoOpenDiscoveryActivityFlag) {
                    openDiscoveryActivity()
                }

            }
            return
        }

        bluetoothSocket?.isConnected?.let {
            if (!it) {
                MainScope().launch {
                    newContext?.let {context ->
                        ToastUtil.toast(context,SerialPortToast.connectFirst)
                    }
                    if (autoOpenDiscoveryActivityFlag) {
                        openDiscoveryActivity()
                    }
                }
                return
            }
        }

        Thread{
            send(data)
        }.start()
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
    fun getPairedDevicesList() = pairedDevicesList

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
    fun getUnPairedDevicesList() = unPairedDevicesList

    /**
     * 获取已配对设备列表
     * @return 已配对设备列表
     * @Author Shanya
     * @Date 2021-6-23
     * @Version 3.1.0
     */
    fun getPairedDevicesListBD() = pairedDevicesListBD

    /**
     * 获取未配对设备列表
     * @return 未配对设备列表
     * @Author Shanya
     * @Date 2021-6-23
     * @Version 3.1.0
     */
    fun getUnPairedDevicesListBD() = unPairedDevicesListBD

    /**
     * 开始搜索
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-5-28
     * @Version 3.1.0
     */
    fun doDiscovery(context: Context) {
        SerialPortDiscovery.startLegacyScan(context)
        SerialPortDiscovery.startBleScan()
    }

    /**
     * 停止搜索
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-5-28
     * @Version 3.1.0
     */
    fun cancelDiscovery(context: Context) {
        SerialPortDiscovery.stopLegacyScan(context)
        SerialPortDiscovery.stopBleScan()
    }
}