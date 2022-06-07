package world.shanya.serialport.strings

import android.widget.Toast
import world.shanya.serialport.R

object SerialPortToast {
    fun get() = this

    var connectSucceeded = SerialPortToastBean(true, R.string.connectSucceededToast,Toast.LENGTH_SHORT)
    var connectFailed = SerialPortToastBean(true,R.string.connectFailedToast,Toast.LENGTH_SHORT)
    var disconnect = SerialPortToastBean(true,R.string.disconnectToast,Toast.LENGTH_SHORT)
    var connectFirst = SerialPortToastBean(true,R.string.connectFirstToast,Toast.LENGTH_SHORT)
    var disconnectFirst = SerialPortToastBean(true,R.string.disconnectFirstToast,Toast.LENGTH_SHORT)
    var permission = SerialPortToastBean(true,R.string.permissionToast,Toast.LENGTH_SHORT)
    var hexTip = SerialPortToastBean(true,R.string.hexTipToast,Toast.LENGTH_SHORT)
    var openBluetoothSucceeded = SerialPortToastBean(true,R.string.openBluetoothSucceededToast,Toast.LENGTH_SHORT)
    var openBluetoothFailed = SerialPortToastBean(true,R.string.openBluetoothFailedToast,Toast.LENGTH_SHORT)

    var openServer = SerialPortToastBean(true, R.string.openServer, Toast.LENGTH_SHORT)
    var closeServer = SerialPortToastBean(true, R.string.closeServer, Toast.LENGTH_SHORT)

}