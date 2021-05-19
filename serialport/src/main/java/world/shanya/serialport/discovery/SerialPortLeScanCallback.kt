package world.shanya.serialport.discovery

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import world.shanya.serialport.SerialPort
import world.shanya.serialport.tools.LogUtil

open class SerialPortLeScanCallback : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)

        SerialPort.logUtil.log("BleScanResult",result?.device?.address.toString())
    }
}