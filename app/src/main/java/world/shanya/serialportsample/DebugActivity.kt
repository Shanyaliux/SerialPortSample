package world.shanya.serialportsample

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_debug.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialport.SerialPortServerBuilder

class DebugActivity : AppCompatActivity() {
    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        val stringBuilder = StringBuilder()

        val serialPort = SerialPortBuilder
            .isDebug(true)
            .setReceivedDataCallback {
                MainScope().launch {
                    stringBuilder.append(it)
                    textViewReceiced.text = stringBuilder.toString()
                }
            }
            .setConnectionStatusCallback { status, bluetoothDevice ->
                MainScope().launch {
                    if (status) {
                        textViewConnectInfo.text =
                            "设备名称:\t${bluetoothDevice?.name}\n" +
                                    "设备地址:\t${bluetoothDevice?.address}\n" +
                                    "设备类型:\t${bluetoothDevice?.type}"

                    }else
                        textViewConnectInfo.text = ""
                }
            }
            .build(this)

        val serialPortServer = SerialPortServerBuilder
            .setServerReceivedDataCallback {
                MainScope().launch {
                    stringBuilder.append(it)
                    textViewReceiced.text = stringBuilder.toString()
                }
            }
            .setServerConnectStatusCallback { status, bluetoothDevice ->
                MainScope().launch {
                    if (status) {
                        textViewConnectInfo.text =
                            "设备名称:\t${bluetoothDevice?.name}\n" +
                                    "设备地址:\t${bluetoothDevice?.address}\n" +
                                    "设备类型:\t${bluetoothDevice?.type}"

                    }else
                        textViewConnectInfo.text = ""
                }
            }
            .build(this)


        buttonConnect.setOnClickListener {
            serialPort.openDiscoveryActivity()
        }


        buttonDisconnect.setOnClickListener {
//            serialPort.disconnect()
            serialPortServer.disconnect()
        }

        buttonSend.setOnClickListener {
//            serialPort.sendData(editTextTextSend.text.toString())
            serialPortServer.sendData(editTextTextSend.text.toString())
        }

        buttonOpenServer.setOnClickListener {
            serialPortServer.openServer()
        }

        buttonCloseServer.setOnClickListener {
            serialPortServer.closeServer()
        }

    }
}