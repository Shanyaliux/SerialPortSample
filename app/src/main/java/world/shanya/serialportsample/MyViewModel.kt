/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.app
 * 类名：MyViewModel.kt
 * 作者：Shanya
 * 日期：2022/3/13 下午10:43
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialportsample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.StringBuilder

class MyViewModel: ViewModel() {
    val deviceInfoLiveData = MutableLiveData<DeviceInfo>()
    val receivedStringBuilder = StringBuilder()
    val receivedLiveData = MutableLiveData<StringBuilder>()
}

data class DeviceInfo(
    val isConnected: Boolean,
    val deviceName:String,
    val deviceAddr:String,
    val deviceType:String
)