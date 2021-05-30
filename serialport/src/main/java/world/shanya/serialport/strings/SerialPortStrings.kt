package world.shanya.serialport.strings

import android.widget.Toast

internal object SerialPortStrings {
    var connectSucceeded = SerialPortToastBean(true,"连接成功",Toast.LENGTH_SHORT)
    var disconnect = SerialPortToastBean(true,"断开连接",Toast.LENGTH_SHORT)
}