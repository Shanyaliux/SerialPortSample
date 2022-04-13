/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.app
 * 类名：MessageFragment.kt
 * 作者：Shanya
 * 日期：2022/3/13 下午9:07
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialportsample.pagers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.fragment_message.view.*
import world.shanya.serialport.SerialPort
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialportsample.MyViewModel
import world.shanya.serialportsample.R


class MessageFragment : Fragment() {

    private val myViewModel: MyViewModel by activityViewModels()

    @SuppressLint("SetTextI18n", "RtlHardcoded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myViewModel.deviceInfoLiveData.observe(this, {
            if (it.isConnected) {
                textViewInfo.gravity = Gravity.LEFT
                textViewInfo.text =
                    "设备名称:\t${it.deviceName}\n设备地址:\t${it.deviceAddr}\n设备类型:\t${it.deviceType}"
            } else {
                textViewInfo.gravity = Gravity.CENTER
                textViewInfo.text = "当前未连接设备"
            }
        })

        myViewModel.receivedLiveData.observe(this, {
            textViewReceiced.text = it.toString()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_message, container, false)

        root.buttonSend.setOnClickListener {
            SerialPortBuilder.sendData(root.editTextTextSend.text.toString())
        }

        root.switchReadType.setOnCheckedChangeListener { _, b ->
            if (b) {
                SerialPortBuilder.setReadDataType(SerialPort.READ_HEX)
                Toast.makeText(requireActivity(), "接收数据格式为十六进制", Toast.LENGTH_SHORT).show()
            } else {
                SerialPortBuilder.setReadDataType(SerialPort.READ_STRING)
                Toast.makeText(requireActivity(), "接收数据格式为字符串", Toast.LENGTH_SHORT).show()
            }
        }

        root.switchSendType.setOnCheckedChangeListener { _, b ->
            if (b) {
                SerialPortBuilder.setSendDataType(SerialPort.SEND_HEX)
                Toast.makeText(requireActivity(), "发送数据格式为十六进制", Toast.LENGTH_SHORT).show()
            } else {
                SerialPortBuilder.setSendDataType(SerialPort.SEND_STRING)
                Toast.makeText(requireActivity(), "发送数据格式为字符串", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }
}