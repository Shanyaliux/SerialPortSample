package world.shanya.serialport.strings

import android.widget.Toast

object SerialPortToast {
    fun get() = this

    var connectSucceeded = SerialPortToastBean(true,"连接成功",Toast.LENGTH_SHORT)
    var connectFailed = SerialPortToastBean(true,"连接失败",Toast.LENGTH_SHORT)
    var disconnect = SerialPortToastBean(true,"断开连接",Toast.LENGTH_SHORT)
    var connectFirst = SerialPortToastBean(true,"请先连接设备",Toast.LENGTH_SHORT)
    var disconnectFirst = SerialPortToastBean(true,"请先断开连接",Toast.LENGTH_SHORT)
    var permission = SerialPortToastBean(true,"请先开启位置权限",Toast.LENGTH_SHORT)
    var hexTip = SerialPortToastBean(true,"请输入的十六进制数据保持两位，不足前面补0",Toast.LENGTH_SHORT)
}