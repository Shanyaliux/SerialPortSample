/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.app
 * 类名：MessageFragment.kt
 * 作者：Shanya
 * 日期：2022/3/13 下午9:07
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialportsample.pagers

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.fragment_message.view.*
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialportsample.MyViewModel
import world.shanya.serialportsample.R
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessageFragment : Fragment() {

    private val myViewModel: MyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myViewModel.deviceInfoLiveData.observe(this, {
            device_info_view.setCenterTopString(it.deviceName)
            device_info_view.setCenterString(it.deviceAddr)
            device_info_view.setCenterBottomString(it.deviceType)
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

        return root
    }
}