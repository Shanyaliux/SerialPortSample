package world.shanya.serilportsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import world.shanya.serialport.SerialPortBuilder


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serialPort = SerialPortBuilder
            .isDebug(true)
            .isIgnoreNoNameDevice(true)
            .build(this)

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
    }
}