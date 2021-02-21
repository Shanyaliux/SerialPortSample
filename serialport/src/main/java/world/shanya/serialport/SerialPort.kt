package world.shanya.serialport

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import world.shanya.serialport.discovery.Device
import world.shanya.serialport.discovery.DiscoveryActivity
import world.shanya.serialport.log.LogUtil

typealias FindUnpairedDeviceCallback = () -> Unit

class SerialPort private constructor() {
    companion object {
        private var instance: SerialPort? = null
            get() {
                if (field == null) {
                    field = SerialPort()
                }
                return field
            }

        @Synchronized
        fun get(): SerialPort {
            return instance!!
        }

        val pairedDevicesList = ArrayList<Device>()
        val unPairedDevicesList = ArrayList<Device>()
        internal val logUtil = LogUtil("SerialPortLog")

        internal val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        private var UUID = "00001101-0000-1000-8000-00805F9B34FB"
        internal var discoveryLiveData = MutableLiveData<Boolean>()

        internal var findUnpairedDeviceCallback: FindUnpairedDeviceCallback ?= null
        fun setFindDeviceListener(findUnpairedDeviceCallback: FindUnpairedDeviceCallback) {
            this.findUnpairedDeviceCallback = findUnpairedDeviceCallback
        }
    }

    init {
        discoveryLiveData.value = false
    }

    fun isDebug(status: Boolean):SerialPort {
        logUtil.status = status
        return this
    }

    fun openDiscoveryActivity(context: Context) {
        context.startActivity(Intent(context,DiscoveryActivity::class.java))
    }
}