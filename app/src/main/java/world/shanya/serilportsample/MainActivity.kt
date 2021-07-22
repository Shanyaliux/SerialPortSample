package world.shanya.serilportsample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialport.strings.SerialPortToast


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serialPort = SerialPortBuilder
            .isDebug(true)
            .autoConnect(false)
            .setAutoReconnectAtIntervals(false,time = 10000)
            .setSendDataType(SerialPort.SEND_HEX)
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
            serialPort.printPossibleBleUUID()
        }

        buttonSend.setOnClickListener {
//            serialPort.sendData("hello\r\n")
            serialPort.sendData("0A 0D")
        }
    }
}