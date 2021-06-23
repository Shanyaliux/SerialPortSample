package world.shanya.serilportsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import world.shanya.serialport.SerialPort
import world.shanya.serialport.SerialPortBuilder


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serialPort = SerialPortBuilder
            .isDebug(true)
            .isIgnoreNoNameDevice(false)
            .setReceivedDataListener {
                Log.d("SerialPortDebug", "onCreate: $it")
            }
            .build(this)

        SerialPort.serialPortToast.connectSucceeded.content = "sd"

        buttonConnect.setOnClickListener {
            serialPort.openDiscoveryActivity()
        }

        buttonDisconnect.setOnClickListener {
            serialPort.disconnect()
        }

        serialPort.setConnectStatusCallback { status, device ->
            if (!status) {
                println(device.name)
            }
        }

        buttonScan.setOnClickListener {
//            serialPort.doDiscoveryBle()
        }

        buttonSend.setOnClickListener {
            serialPort.sendData("hello")
        }
    }
}