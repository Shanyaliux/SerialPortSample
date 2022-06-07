package world.shanya.serialport.tools

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.R
import world.shanya.serialport.SerialPort
import world.shanya.serialport.connect.SerialPortConnect
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.strings.SerialPortToastBean
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.AbstractList

/**
 * SPUtil SharePreferences管理类
 * @Author Shanya & Se7enOrEleven
 * @Date 2021-7-21
 * @Version 4.0.0
 */
@SuppressLint("MissingPermission")
internal object SPUtil {

    private const val SPName = "SerialPortSP"
    private const val DEVICE_NAME = "SP_DEVICE_NAME"
    private const val DEVICE_ADDRESS = "SP_DEVICE_ADDRESS"
    private const val DEVICE_TYPE = "SP_DEVICE_TYPE"

    /**
     * putDeviceInfo 储存设备信息
     * @param context 上下文
     * @param bluetoothDevice 设备
     * @Author Shanya & Se7enOrEleven
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun putDeviceInfo(context: Context, bluetoothDevice: BluetoothDevice?) {
        val sp = context.getSharedPreferences(SPName, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(DEVICE_NAME, bluetoothDevice?.name)
        editor.putString(DEVICE_ADDRESS, bluetoothDevice?.address)
        editor.putString(DEVICE_TYPE, bluetoothDevice?.type.toString())
        editor.apply()
    }

    /**
     * getString 取出字段
     * @param context 上下文
     * @param key 需要去除的字段的对应的key
     * @return 取出的字段
     * @Author Shanya & Se7enOrEleven
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    private fun getString(context: Context, key: String): String? {
        val sp = context.getSharedPreferences(SPName, Context.MODE_PRIVATE)
        return sp.getString(key, "")
    }

    /**
     * getDeviceName 取出设备名称
     * @param context 上下文
     * @return 设备名
     * @Author Shanya & Se7enOrEleven
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun getDeviceName(context: Context): String? {
        return if (TextUtils.isEmpty(getString(context, DEVICE_NAME))) "" else getString(
                context,
                DEVICE_NAME
        )
    }

    /**
     * getDeviceAddress 取出设备地址
     * @param context 上下文
     * @return 设备地址
     * @Author Shanya & Se7enOrEleven
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun getDeviceAddress(context: Context): String? {
        return if (TextUtils.isEmpty(getString(context, DEVICE_ADDRESS))) "" else getString(
            context,
            DEVICE_ADDRESS
        )
    }

    /**
     * getDeviceName 取出设备类型
     * @param context 上下文
     * @return 设备类型
     * @Author Shanya & Se7enOrEleven
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    fun getDeviceType(context: Context): String? {
        return if (TextUtils.isEmpty(getString(context, DEVICE_TYPE))) "" else getString(
            context,
            DEVICE_TYPE
        )
    }
}

/**
 * HexStringToString 十六进制字符串转换成字符串
 * @Author Shanya
 * @Date 2021-3-24
 * @Version 3.0.0
 */
internal object HexStringToString {

    /**
     * charToByte char转换成Byte
     * @param c 待转换char
     * @return 转换成功的Byte
     * @Author Shanya
     * @Date 2021-3-24
     * @Version 3.0.0
     */
    private fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    /**
     * hexStringToBytes 十六进制字符串转换成Byte数组
     * @param hexString 待转换十六进制字符串
     * @return 转换成功的Byte数组
     * @Author Shanya
     * @Date 2021-3-24
     * @Version 3.0.0
     */
    private fun hexStringToBytes(hexString: String): ByteArray? {
        var tempHexString = hexString
        if (tempHexString == "") {
            return null
        }
        tempHexString = tempHexString.replace(" ","")
        tempHexString = tempHexString.toUpperCase(Locale.ROOT)
        val length = tempHexString.length / 2
        val hexChars = tempHexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    /**
     * conversion 转换
     * @param hexString 待转换十六进制字符串
     * @return 转换成功的字符串
     * @Author Shanya
     * @Date 2021-3-24
     * @Version 3.0.0
     */
    fun conversion(hexString: String): String? {
        return hexStringToBytes(hexString)?.let { String(it) }
    }
}

/**
 * LogUtil 日志打印工具类
 * @Author Shanya
 * @Date 2021-3-16
 * @Version 3.0.0
 */
internal object LogUtil {
    var status = false

    fun log(title: String, content: String = "") {
        if (status) {
            if (content != "")
                Log.d("SerialPort", "$title  -->  $content")
            else
                Log.d("SerialPort", title)
        }
    }
}

/**
 * ToastUtil 提示信息工具类
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
internal object ToastUtil {
    fun toast(context: Context, serialPortToastBean: SerialPortToastBean) {
        if (serialPortToastBean.status) {
            MainScope().launch {
                Toast.makeText(
                    context,
                    context.getString(serialPortToastBean.content),
                    serialPortToastBean.time
                ).show()
            }
        }
    }
}


/**
 * 数据处理工具类
 * @Author Shanya
 * @Date 2021-12-10
 * @Version 4.1.2
 */
object DataUtil {
    /**
     * 字符串转换成十六进制
     * @param str 待转换的字符串
     * @return 十六进制数组
     * @Author Shanya
     * @Date 2021-12-10
     * @Version 4.1.2
     */
    fun string2hex(str: String): ArrayList<Byte>? {
        val chars = "0123456789ABCDEF".toCharArray()
        val stringUpper = str.toUpperCase()
        val stingTemp = stringUpper.replace(" ","")
        val bs = stingTemp.toCharArray()
        var bit = 0
        var i = 0
        val intArray = ArrayList<Byte>()
        if (stingTemp.length and 0x01 != 0){
            MainScope().launch {
                SerialPort.newContext?.let {
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

    fun arrayListByte2ByteArray(data: String): ByteArray {
        return string2hex(data)?.toList()!!.toByteArray()
    }
}

