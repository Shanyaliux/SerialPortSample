package world.shanya.serialport

import android.app.Activity
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
import world.shanya.serialport.broadcast.DiscoveryBroadcastReceiver
import world.shanya.serialport.discovery.Device
import world.shanya.serialport.discovery.DiscoveryActivity
import world.shanya.serialport.service.SerialPortService
import world.shanya.serialport.tools.HexStringToString
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.SPUtil
import java.io.IOException
import java.io.InputStream
import java.util.UUID.*
import kotlin.collections.ArrayList
import java.lang.Class as Class


//找到设备接口
typealias FindUnpairedDeviceCallback = () -> Unit
//连接状态接口
typealias ConnectStatusCallback = (status: Boolean, device: Device) -> Unit
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

        //已配对设备列表
        internal val pairedDevicesList = ArrayList<Device>()
        //未配对设备列表
        internal val unPairedDevicesList = ArrayList<Device>()

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
        private val discoveryBroadcastReceiver = DiscoveryBroadcastReceiver()
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
        private var UUID = "00001101-0000-1000-8000-00805F9B34FB"
        //搜索状态LiveData
        internal var discoveryStatusLiveData = MutableLiveData<Boolean>()
        //新的上下文
        private var newContext: Context ?= null
        //旧的上下文
        private var oldContext: Context ?= null
        //连接广播接收器
        private val connectBroadcastReceiver = ConnectBroadcastReceiver()
        //连接状态
        internal var connectStatus = false
        //自动连接标志（执行自动连接后即为 true）
        internal var autoConnectFlag = false
        //已经连接的设备
        internal var connectedDevice: Device ?= null
        //十六进制字符串转换成字符串标志
        internal var hexStringToStringFlag = false
        //自动打开搜索页面标志
        internal var autoOpenDiscoveryActivity = false

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
         * 内连接函数
         * @param device 连接设备
         * @Author Shanya
         * @Date 2021-3-16
         * @Version 3.0.0
         */
        internal fun _connectDevice(device: Device) {
            Thread {
                val address = device.address
                val bluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
                bluetoothSocket?.isConnected?.let {
                    if (it) {
                        connectCallback?.invoke()
                        if (autoConnectFlag){
                            MainScope().launch {
                                Toast.makeText(newContext, "请先断开当前连接", Toast.LENGTH_SHORT).show()
                            }
                        } else {

                        }

                    } else {
                        try {
                            bluetoothSocket =
                                    bluetoothDevice.createRfcommSocketToServiceRecord(fromString(UUID))
                            bluetoothSocket?.connect()

                            newContext?.let {context->
                                SPUtil.putString(context,device)
                            }
                            connectCallback?.invoke()
                            connectStatusCallback?.invoke(true,device)
                            connectedDevice = device
                            connectStatus = true
                            logUtil.log("SerialPort","连接成功")
                            MainScope().launch {
                                Toast.makeText(newContext,"连接成功", Toast.LENGTH_SHORT).show()
                            }

                            inputStream = bluetoothSocket?.inputStream
                            newContext?.startService(Intent(newContext,SerialPortService::class.java))
                        } catch (e: IOException) {
                            logUtil.log("SerialPort","连接失败")
                            MainScope().launch {
                                Toast.makeText(newContext,"连接失败", Toast.LENGTH_SHORT).show()
                            }
                            connectStatus = false
                            connectCallback?.invoke()
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
                        connectCallback?.invoke()
                        connectStatusCallback?.invoke(true,device)
                        connectedDevice = device
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
                        connectCallback?.invoke()
                        try {
                            bluetoothSocket?.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                }
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
        internal fun _hexStringToString(hexString: String): String? {
            return HexStringToString.conversion(hexString)
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
        oldContext = newContext
        newContext = context
        oldContext?.unregisterReceiver(connectBroadcastReceiver)
        newContext?.registerReceiver(
            connectBroadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        )
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
                Toast.makeText(newContext,"请输入的十六进制数据保持两位，不足前面补0",Toast.LENGTH_SHORT).show()
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

//            for (bo in bos) {
//                if (bo.toInt() == 0x0a) {
//                    n++
//                }
//            }
//            val bosNew = ByteArray(bos.size + n)
//            n = 0
//            for (bo in bos) {
//                if (bo.toInt() == 0x0a) {
//                    bosNew[n++] = 0x0d
//                    bosNew[n] = 0x0a
//                } else {
//                    bosNew[n] = bo
//                }
//                n++
//            }

            outputStream?.write(bos)
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
        val intent = Intent(newContext,DiscoveryActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        newContext?.startActivity(intent)
    }

    /**
     * 打开搜索页面
     * @param intent
     * @Author Shanya
     * @Date 2021-3-26
     * @Version 3.0.0
     */
    fun openDiscoveryActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        newContext?.startActivity(intent)
    }

    /**
     * 断开连接
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun disconnect() {
        try {
            connectStatus = false
            bluetoothSocket?.close()
            newContext?.stopService(Intent(newContext,SerialPortService::class.java))

        }catch (e: IOException){
            e.printStackTrace()
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
     * 连接设备
     * @param address 设备地址
     * @Author Shanya
     * @Date 2021-3-16
     * @Version 3.0.0
     */
    fun connectDevice(address: String) {
        _connectDevice(Device("",address))
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
     * @Date 2021-5-27
     * @Version 3.0.2
     */
    fun sendData(data:String){
        if (!bluetoothAdapter.isEnabled){
            bluetoothAdapter.enable()
            return
        }

        if (bluetoothSocket == null) {
            MainScope().launch {
                Toast.makeText(newContext,"请先连接设备",Toast.LENGTH_SHORT).show()
                if (autoOpenDiscoveryActivity) {
                    openDiscoveryActivity()
                }

            }
            return
        }

        bluetoothSocket?.isConnected?.let {
            if (!it) {
                MainScope().launch {
                    Toast.makeText(newContext,"请先连接设备",Toast.LENGTH_SHORT).show()
                    if (autoOpenDiscoveryActivity) {
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
    fun getPairedDevicesList() = pairedDevicesList

    /**
     * 获取未配对设备列表
     * @return 未配对设备列表
     * @Author Shanya
     * @Date 2021-3-26
     * @Version 3.0.0
     */
    fun getUnPairedDevicesList() = unPairedDevicesList

    /**
     * 搜索
     * @param context 上下文
     * @Author Shanya
     * @Date 2021-3-26
     * @Version 3.0.0
     */
    fun doDiscovery(context: Context) {

        logUtil.log("Discovery","RegisterReceiver")

        context.registerReceiver(discoveryBroadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        context.registerReceiver(discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        context.registerReceiver(discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))

        logUtil.log("Discovery","Get paired devices")

        val pairedDevices:Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
        if (pairedDevices.isNotEmpty()){
            pairedDevicesList.clear()
            for (device in pairedDevices){
                pairedDevicesList.add(Device(device.name?:"unknown",device.address))
            }
        }

        logUtil.log("Discovery","Start discovery")

        unPairedDevicesList.clear()

        bluetoothAdapter.startDiscovery()
    }
}