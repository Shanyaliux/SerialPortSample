package world.shanya.serialportsample

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
import world.shanya.serialport.SerialPortConfig
import world.shanya.serialport.tools.SerialPortTools


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val config = SerialPortConfig()
        config.debug = true
        config.autoConnect = false
        config.openConnectionTypeDialogFlag = false
        config.autoReconnect = false
        config.reconnectAtIntervals = 10000
        config.ignoreNoNameDevice = false
        config.UUID_BLE = "sad"
        config.UUID_BLE_READ = "0000ffe1-0000-1000-8000-00805f9b34fb"
        config.UUID_BLE_SEND = "0000ffe1-0000-1000-8000-00805f9b34fb"

        val serialPort = SerialPortBuilder
            .setConfig(config)
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

//        serialPort.setSendDataType(SerialPort.SEND_HEX)
//        serialPort.setReadDataType(SerialPort.READ_HEX)

        buttonSend.setOnClickListener {

            serialPort.sendData("0f ff")
        }
    }
}