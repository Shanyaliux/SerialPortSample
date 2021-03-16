package world.shanya.serialport.tools

import android.content.Context
import android.text.TextUtils
import world.shanya.serialport.discovery.Device

const val SPName = "SerialPortSP"
const val DEVICE_NAME = "SP_DEVICE_NAME"
const val DEVICE_ADDRESS = "SP_DEVICE_ADDRESS"

/**
 * SPUtil SharePreferences管理类
 * @Author SPUtil
 * @Date 2020-11-25
 * @Version 3.0.0
 */
object SPUtil {
    fun putString(context: Context, device: Device) {
        val sp = context.getSharedPreferences(SPName, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(DEVICE_NAME, device.name)
        editor.putString(DEVICE_ADDRESS, device.address)
        editor.apply()
    }

    private fun getString(context: Context, key: String): String? {
        val sp = context.getSharedPreferences(SPName, Context.MODE_PRIVATE)
        return sp.getString(key, "")
    }

    fun clearSp(context: Context) {
        val sp = context.getSharedPreferences(SPName, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        editor.apply()
    }

    fun getSPDeviceName(context: Context): String? {
        return if (TextUtils.isEmpty(getString(context, DEVICE_NAME))) "" else getString(
                context,
                DEVICE_NAME
        )
    }

    fun getSPDeviceAddress(context: Context): String? {
        return if (TextUtils.isEmpty(getString(context, DEVICE_ADDRESS))) "" else getString(
                context,
                DEVICE_ADDRESS
        )
    }

    fun getSPDevice(context: Context): Device? {
        val name = getSPDeviceName(context)
        val address = getSPDeviceAddress(context)
        return if (address == "") null else Device(name!!,address!!)
    }
}