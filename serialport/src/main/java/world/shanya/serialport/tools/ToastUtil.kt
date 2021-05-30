package world.shanya.serialport.tools

import android.content.Context
import android.widget.Toast
import world.shanya.serialport.strings.SerialPortToastBean

internal object ToastUtil {
    fun toast(context: Context, serialPortToastBean: SerialPortToastBean) {
        if (serialPortToastBean.status) {
            Toast.makeText(context,serialPortToastBean.content,serialPortToastBean.time).show()
        }
    }
}