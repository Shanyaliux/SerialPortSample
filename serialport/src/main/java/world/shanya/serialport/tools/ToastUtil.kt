package world.shanya.serialport.tools

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.strings.SerialPortToastBean

internal object ToastUtil {
    fun toast(context: Context, serialPortToastBean: SerialPortToastBean) {
        if (serialPortToastBean.status) {
            MainScope().launch {
                Toast.makeText(context,serialPortToastBean.content,serialPortToastBean.time).show()
            }
        }
    }
}