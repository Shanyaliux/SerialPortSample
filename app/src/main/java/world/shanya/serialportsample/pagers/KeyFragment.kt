/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.app
 * 类名：KeyFragment.kt
 * 作者：Shanya
 * 日期：2022/3/13 下午9:16
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialportsample.pagers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.fragment_key.view.*
import kotlinx.android.synthetic.main.key_button_info_dialog.view.*
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialportsample.R
import world.shanya.serialportsample.utils.KeyButtonInfo
import world.shanya.serialportsample.utils.KeySpTools



class KeyFragment : Fragment() {

    private var isEditMode = false
    private val keyButtonInfoHashMap = HashMap<Int, KeyButtonInfo>()
    private val keyButtonIds = arrayOf(
        R.id.key_button1, R.id.key_button2, R.id.key_button3,
        R.id.key_button4, R.id.key_button5, R.id.key_button6,
        R.id.key_button7, R.id.key_button8, R.id.key_button9,
        R.id.key_button10, R.id.key_button11, R.id.key_button12,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_key, container, false)

        //读取按键信息
        for (keyButtonId in keyButtonIds) {
            keyButtonInfoHashMap[keyButtonId] = KeySpTools.getButtonInfo(requireActivity(), keyButtonId)
            val button = root.findViewById<Button>(keyButtonId)
            button.text = keyButtonInfoHashMap[keyButtonId]?.buttonName?:""
        }

        root.switchEditMode.setOnCheckedChangeListener { _, b ->
            isEditMode = b
        }

        root.key_button1.setOnClickListener(keyButtonListener)
        root.key_button2.setOnClickListener(keyButtonListener)
        root.key_button3.setOnClickListener(keyButtonListener)
        root.key_button4.setOnClickListener(keyButtonListener)
        root.key_button5.setOnClickListener(keyButtonListener)
        root.key_button6.setOnClickListener(keyButtonListener)
        root.key_button7.setOnClickListener(keyButtonListener)
        root.key_button8.setOnClickListener(keyButtonListener)
        root.key_button9.setOnClickListener(keyButtonListener)
        root.key_button10.setOnClickListener(keyButtonListener)
        root.key_button11.setOnClickListener(keyButtonListener)
        root.key_button12.setOnClickListener(keyButtonListener)

        return root
    }

    private val keyButtonListener = View.OnClickListener {
        if (isEditMode) {
            openDialog(it.id)
        } else {
            SerialPortBuilder.sendData(keyButtonInfoHashMap[it.id]?.buttonData?:"")
        }

    }

    private fun openDialog(id: Int) {
        MaterialDialog(requireActivity(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.key_button_info_dialog)
            getCustomView().editTextTextButtonName.setText(keyButtonInfoHashMap[id]?.buttonName?:"")
            getCustomView().editTextTextButtonData.setText(keyButtonInfoHashMap[id]?.buttonData?:"")
            positiveButton {
                val keyButtonInfo = KeyButtonInfo(getCustomView().editTextTextButtonName.text.toString(),
                    getCustomView().editTextTextButtonData.text.toString())
                KeySpTools.putButtonInfo(requireActivity(), id, keyButtonInfo)
                keyButtonInfoHashMap[id] = keyButtonInfo
                val button = requireActivity().findViewById<Button>(id)
                button.text = keyButtonInfo.buttonName
            }
            negativeButton {

            }
        }
    }

}