package world.shanya.serilportsample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialport.tools.SerialPortTools


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serialPort = SerialPortBuilder
            .isDebug(true)
            .isIgnoreNoNameDevice(true)
            .setReceivedDataListener {
                Log.d("SerialPortDebug", "onCreate: $it")
            }
            .setConnectionStatusCallback { status, bluetoothDevice ->
                MainScope().launch {
                    textViewInfo.text =
                        "status : $status \n" +
                                "bluetoothDevice : ${bluetoothDevice?.type} \n"
                }
            }
            .build(this)



        buttonConnect.setOnClickListener {
            serialPort.openDiscoveryActivity()
        }

        buttonDisconnect.setOnClickListener {
            serialPort.disconnect()
        }

        buttonScan.setOnClickListener {

        }

        buttonSend.setOnClickListener {
            serialPort.sendData("hello\r\n")
        }
    }
}