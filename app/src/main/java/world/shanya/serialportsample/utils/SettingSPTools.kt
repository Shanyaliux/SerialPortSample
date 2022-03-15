/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.app
 * 类名：SPTools.kt
 * 作者：Shanya
 * 日期：2022/3/13 下午8:42
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialportsample.utils

import android.content.Context
import world.shanya.serialport.SerialPortConfig

/**
 * 设置偏好存储类
 */
object SettingSPTools {
    private const val SP_SettingName = "SerialPortSetting"
    const val SPP_UUID = "SPP_UUID"
    const val BLE_R_UUID = "BLE_R_UUID"
    const val BLE_S_UUID = "BLE_S_UUID"
    const val AUTO_CONNECT = "AUTO_CONNECT"
    const val RECONNECT = "RECONNECT"
    const val INTERVALS_TIME = "INTERVALS_TIME"
    const val IGNORE_NO_NAME = "IGNORE_NO_NAME"
    const val CONNECTION_SELECT = "CONNECTION_SELECT"
    const val OPEN_DISCOVERY = "OPEN_DISCOVERY"
    const val HEX_TO_STRING = "HEX_TO_STRING"

    fun putData(context: Context, name:String, data: String) {
        val sp = context.getSharedPreferences(SP_SettingName, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(name, data)
        editor.apply()
    }

    fun getString(context: Context, key: String): String? {
        val sp = context.getSharedPreferences(SP_SettingName, Context.MODE_PRIVATE)
        return sp.getString(key, "")
    }

    fun getSerialPortConfig(context: Context): SerialPortConfig {
        val config = SerialPortConfig()
        if (getString(context, SPP_UUID) == "") {
            config.UUID_LEGACY = "00001101-0000-1000-8000-00805F9B34FB"
        } else {
            config.UUID_LEGACY = getString(context, SPP_UUID)
        }

        if (getString(context, BLE_R_UUID) == "") {
            config.UUID_BLE_READ = "0000ffe1-0000-1000-8000-00805f9b34fb"
        } else {
            config.UUID_BLE_READ = getString(context, BLE_R_UUID)
        }

        if (getString(context, BLE_S_UUID) == "") {
            config.UUID_BLE_SEND = "0000ffe1-0000-1000-8000-00805f9b34fb"
        } else {
            config.UUID_BLE_SEND = getString(context, BLE_S_UUID)
        }

        config.autoConnect = getSwitchSetting(context, AUTO_CONNECT)

        config.autoReconnect = getSwitchSetting(context, RECONNECT)

        if (getString(context, INTERVALS_TIME) == "") {
            config.reconnectAtIntervals = 10000
        } else {
            config.reconnectAtIntervals = getString(context, INTERVALS_TIME)?.toInt() ?: 10000
        }

        config.ignoreNoNameDevice = getSwitchSetting(context, IGNORE_NO_NAME)

        config.openConnectionTypeDialogFlag = getSwitchSetting(context, CONNECTION_SELECT)

        config.autoOpenDiscoveryActivity = getSwitchSetting(context, OPEN_DISCOVERY)

        config.autoHexStringToString = getSwitchSetting(context, HEX_TO_STRING)
        return config
    }

    fun getSwitchSetting(context: Context, switchLabel: String): Boolean {
        return if (getString(context, switchLabel) == "") {
            false
        } else {
            getString(context, switchLabel) != "false"
        }
    }

}