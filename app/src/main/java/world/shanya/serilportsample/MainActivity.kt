package world.shanya.serilportsample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialport.message.MessageManager
import world.shanya.serialport.tools.SPUtil


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serialPort = SerialPortBuilder.isDebug(true).autoConnect(true).build(this)

        val device = SPUtil.getSPDevice(this)

        MessageManager.getInstance()
            .registerMessageReceiver(
                this, "user_info_change"
            ) { msg ->
                if (msg != null) {
                    val nickname = msg.data.getString("nickname", "")
                    Log.d("SerialPortDebug", "onCreate: ${nickname}")
                }
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