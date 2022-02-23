package world.shanya.serialportsample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.xuexiang.xupdate.easy.EasyUpdate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.SerialPortBuilder


class MainActivity : AppCompatActivity() {

    private var serialPort:SerialPort ?= null
    private val jsonUrl = "https://gitee.com/Shanya/serialportappupdate/raw/master/update.json"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EasyUpdate.create(this, jsonUrl)
            .supportBackgroundUpdate(true)
            .update()

        val config = SPTools.getSerialPortConfig(this)
        val stringBuilder = StringBuilder()

        buttonDisconnect.isEnabled = false

        serialPort = SerialPortBuilder
            .isDebug(true)
            .setConfig(config)
            .setReceivedDataCallback {
                stringBuilder.append(it)
                textViewReceiced.text = stringBuilder.toString()
            }
            .setConnectionStatusCallback { status, bluetoothDevice ->
                MainScope().launch {
                    buttonConnect.isEnabled = !status
                    buttonSetting.isEnabled = !status
                    buttonDisconnect.isEnabled = status
                    if (status == false) {
                        textViewName.text = ""
                        textViewAddress.text = ""
                        textViewType.text = ""
                    } else {
                        textViewName.text = bluetoothDevice?.name
                        textViewAddress.text = bluetoothDevice?.address
                        textViewType.text = bluetoothDevice?.type.toString()
                    }

                }

            }
            .build(this)

        buttonSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        buttonConnect.setOnClickListener {
            serialPort?.openDiscoveryActivity()
        }

        buttonDisconnect.setOnClickListener {
            serialPort?.disconnect()
        }

        switchReceiveType.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                serialPort?.setReadDataType(SerialPort.READ_HEX)
            } else {
                serialPort?.setReadDataType(SerialPort.READ_STRING)
            }
        }

        switchSendType.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                serialPort?.setSendDataType(SerialPort.SEND_HEX)
            } else {
                serialPort?.setSendDataType(SerialPort.SEND_STRING)
            }
        }

        buttonSend.setOnClickListener {
            serialPort?.sendData(editTextTextSend.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        serialPort = SerialPortBuilder.setConfig(SPTools.getSerialPortConfig(this)).build(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_about) {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        if (item.itemId == R.id.menu_check_update) {
            EasyUpdate.create(this, jsonUrl)
                .supportBackgroundUpdate(true)
                .update()
        }
        return super.onOptionsItemSelected(item)
    }
}