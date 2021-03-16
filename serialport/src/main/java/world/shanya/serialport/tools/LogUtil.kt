package world.shanya.serialport.tools

import android.util.Log

/**
 * LogUtil 日志打印工具类
 * @Author Shanya
 * @Date 2021-3-16
 * @Version 3.0.0
 */
class LogUtil constructor(
    private val tag: String
) {
    var status = false

    fun log(title: String, content: String) {
        if (status) {
            Log.d(tag, "$title  -->  $content")
        }
    }
}