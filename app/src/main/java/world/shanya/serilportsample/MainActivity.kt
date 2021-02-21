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
            .setReadDataType(SerialPort.READ_HEX)
            .build(this)
        val device = SPUtil.getSPDevice(this)



        SerialPort.setReceivedDataListener {
            Log.d("SerialPortDebug", "received: ${it}")
        }

        button.setOnClickListener {
            serialPort.openDiscoveryActivity()
        }

        button2.setOnClickListener {
            serialPort.disconnect()
        }

        button3.setOnClickListener {
            startActivity(Intent(this, MainActivityA::class.java))
        }

        button4.setOnClickListener {
            startActivity(Intent(this,MainActivityB::class.java))
        }
    }
}