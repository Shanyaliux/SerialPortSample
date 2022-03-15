/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.app
 * 类名：KeySpTools.kt
 * 作者：Shanya
 * 日期：2022/3/15 下午11:58
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialportsample.utils

import android.content.Context

data class KeyButtonInfo(
    val buttonName: String,
    val buttonData: String
)

object KeySpTools {
    private const val SP_KeyInfo = "SerialPortKey"

    fun putButtonInfo(context: Context, id:Int, keyButtonInfo: KeyButtonInfo) {
        val sp = context.getSharedPreferences(SP_KeyInfo, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(id.toString() + "name", keyButtonInfo.buttonName)
        editor.putString(id.toString() + "data", keyButtonInfo.buttonData)
        editor.apply()
    }

    fun getButtonInfo(context: Context, id: Int): KeyButtonInfo {
        val sp = context.getSharedPreferences(SP_KeyInfo, Context.MODE_PRIVATE)
        return KeyButtonInfo(sp.getString(id.toString() + "name", "")?:"",
            sp.getString(id.toString() + "data", "")?:"")
    }
}