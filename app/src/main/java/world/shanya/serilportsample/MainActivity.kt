package world.shanya.serilportsample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import world.shanya.serialport.SerialPort
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialport.tools.SPUtil


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serialPort = SerialPortBuilder
            .isDebug(true)
            .autoConnect(true)
            .autoHexStringToString(true)
            .setReadDataType(SerialPort.READ_HEX)
            .setReceivedDataListener {
                Log.d("SerialPortDebug", "received: ${it}")
            }
            .setConnectStatusCallback { status, device ->
                if (status) {
                    Log.d("SerialPortDebug", "连接: ${device.address}")
                } else {
                    Log.d("SerialPortDebug", "断开")
                }
            }
            .build(this)

        button.setOnClickListener {
            serialPort.openDiscoveryActivity()
        }

        button2.setOnClickListener {
            serialPort.disconnect()
        }

        button3.setOnClickListener {
//            startActivity(Intent(this, MainActivityA::class.java))
            serialPort.connectDevice("98:D3:32:21:67:D0")
        }

        button4.setOnClickListener {
//            startActivity(Intent(this,MainActivityB::class.java))
            serialPort.sendData("hello")
        }
    }
}