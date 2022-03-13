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
import world.shanya.serialportsample.utils.SPTools

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        editTextTextSPPUUID.addTextChangedListener {
            SPTools.putData(this, SPTools.SPP_UUID, it.toString())
        }

        editTextTextBLEUUIDR.addTextChangedListener {
            SPTools.putData(this, SPTools.BLE_R_UUID, it.toString())
        }

        editTextTextBLEUUIDS.addTextChangedListener {
            SPTools.putData(this, SPTools.BLE_S_UUID, it.toString())
        }

        switchAutoConnect.setOnCheckedChangeListener { _, b ->
            SPTools.putData(this, SPTools.AUTO_CONNECT, b.toString())
        }

        switchAutoReconnectAtIntervals.setOnCheckedChangeListener { _, b ->
            SPTools.putData(this, SPTools.RECONNECT, b.toString())
        }

        editTextIntervalsTime.addTextChangedListener {
            SPTools.putData(this, SPTools.INTERVALS_TIME, it.toString())
        }

        switchIgnoreNoNameDevice.setOnCheckedChangeListener { _, b ->
            SPTools.putData(this, SPTools.IGNORE_NO_NAME, b.toString())
        }

        switchConnectionTypeSelect.setOnCheckedChangeListener { _, b ->
            SPTools.putData(this, SPTools.CONNECTION_SELECT, b.toString())
        }

        switchOpenDiscoveryActivity.setOnCheckedChangeListener { _, b ->
            SPTools.putData(this, SPTools.OPEN_DISCOVERY, b.toString())
        }

        switchHexStringToString.setOnCheckedChangeListener { _, b ->
            SPTools.putData(this, SPTools.HEX_TO_STRING, b.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        initSetting()
    }

    private fun initSetting() {
        if (SPTools.getString(this, SPTools.SPP_UUID) == "") {
            editTextTextSPPUUID.setText("00001101-0000-1000-8000-00805F9B34FB")
        } else {
            editTextTextSPPUUID.setText(SPTools.getString(this, SPTools.SPP_UUID))
        }

        if (SPTools.getString(this, SPTools.BLE_R_UUID) == "") {
            editTextTextBLEUUIDR.setText("0000ffe1-0000-1000-8000-00805f9b34fb")
        } else {
            editTextTextBLEUUIDR.setText(SPTools.getString(this, SPTools.BLE_R_UUID))
        }

        if (SPTools.getString(this, SPTools.BLE_S_UUID) == "") {
            editTextTextBLEUUIDS.setText("0000ffe1-0000-1000-8000-00805f9b34fb")
        } else {
            editTextTextBLEUUIDS.setText(SPTools.getString(this, SPTools.BLE_S_UUID))
        }

        switchAutoConnect.isChecked = SPTools.getSwitchSetting(this, SPTools.AUTO_CONNECT)

        switchAutoReconnectAtIntervals.isChecked = SPTools.getSwitchSetting(this, SPTools.RECONNECT)

        if (SPTools.getString(this, SPTools.INTERVALS_TIME) == "") {
            editTextIntervalsTime.setText("10000")
        } else {
            editTextIntervalsTime.setText(SPTools.getString(this, SPTools.INTERVALS_TIME))
        }

        switchIgnoreNoNameDevice.isChecked = SPTools.getSwitchSetting(this, SPTools.IGNORE_NO_NAME)

        switchConnectionTypeSelect.isChecked = SPTools.getSwitchSetting(this, SPTools.CONNECTION_SELECT)

        switchOpenDiscoveryActivity.isChecked = SPTools.getSwitchSetting(this, SPTools.OPEN_DISCOVERY)

        switchHexStringToString.isChecked = SPTools.getSwitchSetting(this, SPTools.HEX_TO_STRING)
    }


}