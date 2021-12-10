package world.shanya.serilportsample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialport.tools.SerialPortTools


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val serialPort = SerialPortBuilder
            .isDebug(true)
            .autoConnect(true)
            .setAutoReconnectAtIntervals(false, time = 10000)
            .setSendDataType(SerialPort.SEND_STRING)
            .isIgnoreNoNameDevice(false)
            .setDiscoveryStatusWithTypeCallback { deviceType, status ->
                Log.d("SerialPort", "DiscoveryStatusWithType: $deviceType -- $status")
            }
            .setDiscoveryStatusCallback {
                Log.d("SerialPort", "DiscoveryStatus: $it")
            }
            .setReceivedDataCallback {
                Log.d("SerialPort", "ReceivedData: $it")
                MainScope().launch {
                    textViewInfo.text = it
                }
            }
            .setReceivedBytesCallback {
                MainScope().launch {
                    Toast.makeText(
                        this@MainActivity,
                        SerialPortTools.bytes2string(it, "GBK"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
//            .setConnectionResultCallback { result, bluetoothDevice ->
//                Log.d("SerialPort", "ConnectionResult: $result")
//                if (bluetoothDevice != null) {
//                    Log.d("SerialPort", "ConnectionResult: ${bluetoothDevice.address}")
//                } else {
//                    Log.d("SerialPort", "ConnectionResult: null")
//                }
//            }
            .setConnectionStatusCallback { status, bluetoothDevice ->
                MainScope().launch {
                    textViewInfo.text =
                        "status : $status \n" +
                                "bluetoothDevice : ${bluetoothDevice?.type} \n"
                }
            }
            .build(this)

        serialPort.setConnectionResultCallback { result, bluetoothDevice ->
                Log.d("SerialPort", "ConnectionResult: $result")
                if (bluetoothDevice != null) {
                    Log.d("SerialPort", "ConnectionResult: ${bluetoothDevice.address}")
                } else {
                    Log.d("SerialPort", "ConnectionResult: null")
                }
        }

        buttonConnect.setOnClickListener {
            serialPort.openDiscoveryActivity()
        }

        buttonDisconnect.setOnClickListener {
            serialPort.disconnect()
        }

        buttonScan.setOnClickListener {
//            serialPort.printPossibleBleUUID()
            serialPort.connectDevice("F8:33:31:A9:A2:94")
        }

        buttonSend.setOnClickListener {
//            serialPort.sendData("hello\r\n")
            serialPort.sendData("你好")
        }
    }
}