package world.shanya.serilportsample

import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import world.shanya.serialport.SerialPort
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialport.discovery.DiscoveryActivity
import world.shanya.serialport.discovery.SerialPortLeScanCallback
import world.shanya.serialport.tools.SPUtil


class MainActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter

    }


    val scanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
//            Log.d("TAG", "onScanResult: ${result?.device?.name} ${result?.device?.uuids}")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serialPort = SerialPortBuilder
            .isDebug(true).build(this)

        button.setOnClickListener {

            serialPort.openDiscoveryActivity()
        }

        buttonScan.setOnClickListener {
            serialPort.doDiscoveryBle()
        }
    }
}