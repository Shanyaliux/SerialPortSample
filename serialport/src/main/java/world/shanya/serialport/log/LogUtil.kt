package world.shanya.serialport.log

import android.util.Log

class LogUtil constructor(
    private val tag: String,
    private val status: Boolean
) {
    fun log(title: String, content: String) {
        if (status) {
            Log.d(tag, "$title  -->  $content")
        }
    }
}