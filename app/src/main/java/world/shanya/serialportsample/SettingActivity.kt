/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.app
 * 类名：SettingActivity.kt
 * 作者：Shanya
 * 日期：2022/2/23 下午1:10
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialportsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_setting.*
import world.shanya.serialportsample.utils.SettingSPTools

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        editTextTextSPPUUID.addTextChangedListener {
            SettingSPTools.putData(this, SettingSPTools.SPP_UUID, it.toString())
        }

        editTextTextBLEUUIDR.addTextChangedListener {
            SettingSPTools.putData(this, SettingSPTools.BLE_R_UUID, it.toString())
        }

        editTextTextBLEUUIDS.addTextChangedListener {
            SettingSPTools.putData(this, SettingSPTools.BLE_S_UUID, it.toString())
        }

        switchAutoConnect.setOnCheckedChangeListener { _, b ->
            SettingSPTools.putData(this, SettingSPTools.AUTO_CONNECT, b.toString())
        }

        switchAutoReconnectAtIntervals.setOnCheckedChangeListener { _, b ->
            SettingSPTools.putData(this, SettingSPTools.RECONNECT, b.toString())
        }

        editTextIntervalsTime.addTextChangedListener {
            SettingSPTools.putData(this, SettingSPTools.INTERVALS_TIME, it.toString())
        }

        switchIgnoreNoNameDevice.setOnCheckedChangeListener { _, b ->
            SettingSPTools.putData(this, SettingSPTools.IGNORE_NO_NAME, b.toString())
        }

        switchConnectionTypeSelect.setOnCheckedChangeListener { _, b ->
            SettingSPTools.putData(this, SettingSPTools.CONNECTION_SELECT, b.toString())
        }

        switchOpenDiscoveryActivity.setOnCheckedChangeListener { _, b ->
            SettingSPTools.putData(this, SettingSPTools.OPEN_DISCOVERY, b.toString())
        }

        switchHexStringToString.setOnCheckedChangeListener { _, b ->
            SettingSPTools.putData(this, SettingSPTools.HEX_TO_STRING, b.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        initSetting()
    }

    private fun initSetting() {
        if (SettingSPTools.getString(this, SettingSPTools.SPP_UUID) == "") {
            editTextTextSPPUUID.setText("00001101-0000-1000-8000-00805F9B34FB")
        } else {
            editTextTextSPPUUID.setText(SettingSPTools.getString(this, SettingSPTools.SPP_UUID))
        }

        if (SettingSPTools.getString(this, SettingSPTools.BLE_R_UUID) == "") {
            editTextTextBLEUUIDR.setText("0000ffe1-0000-1000-8000-00805f9b34fb")
        } else {
            editTextTextBLEUUIDR.setText(SettingSPTools.getString(this, SettingSPTools.BLE_R_UUID))
        }

        if (SettingSPTools.getString(this, SettingSPTools.BLE_S_UUID) == "") {
            editTextTextBLEUUIDS.setText("0000ffe1-0000-1000-8000-00805f9b34fb")
        } else {
            editTextTextBLEUUIDS.setText(SettingSPTools.getString(this, SettingSPTools.BLE_S_UUID))
        }

        switchAutoConnect.isChecked = SettingSPTools.getSwitchSetting(this, SettingSPTools.AUTO_CONNECT)

        switchAutoReconnectAtIntervals.isChecked = SettingSPTools.getSwitchSetting(this, SettingSPTools.RECONNECT)

        if (SettingSPTools.getString(this, SettingSPTools.INTERVALS_TIME) == "") {
            editTextIntervalsTime.setText("10000")
        } else {
            editTextIntervalsTime.setText(SettingSPTools.getString(this, SettingSPTools.INTERVALS_TIME))
        }

        switchIgnoreNoNameDevice.isChecked = SettingSPTools.getSwitchSetting(this, SettingSPTools.IGNORE_NO_NAME)

        switchConnectionTypeSelect.isChecked = SettingSPTools.getSwitchSetting(this, SettingSPTools.CONNECTION_SELECT)

        switchOpenDiscoveryActivity.isChecked = SettingSPTools.getSwitchSetting(this, SettingSPTools.OPEN_DISCOVERY)

        switchHexStringToString.isChecked = SettingSPTools.getSwitchSetting(this, SettingSPTools.HEX_TO_STRING)
    }


}