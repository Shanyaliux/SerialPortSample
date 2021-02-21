package world.shanya.serialport.tools

import android.util.Log

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