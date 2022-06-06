package world.shanya.serialport.connect

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import world.shanya.serialport.SerialPort
import world.shanya.serialport.server.SerialPortServer
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.strings.SerialPortToastBean
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.ToastUtil

/**
 * BluetoothStatusBroadcastReceiver 蓝牙连接状态变更广播接收器
 * 在这里只对传统蓝牙的断开进行处理
 * 实时监听蓝牙断开，若蓝牙断开则自动打开
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
@SuppressLint("MissingPermission")
class BluetoothStatusBroadcastReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                SerialPortConnect.connectedLegacyDevice?.let {
                    SerialPort._legacyDisconnect()
                }
                SerialPortServer.connectedDevice?.let {
                    SerialPortServer.__disconnect()
                }
            }

            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                    BluetoothAdapter.STATE_TURNING_ON -> {
                        LogUtil.log("蓝牙打开成功")
                        context?.let {
                            ToastUtil.toast(it, SerialPortToast.openBluetoothSucceeded)
                        }
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        if (!SerialPort.bluetoothAdapter.enable()) {
                            LogUtil.log("蓝牙打开失败")
                            context?.let {
                                ToastUtil.toast(it, SerialPortToast.openBluetoothFailed)
                            }
                        }
                    }
                }
            }
        }
    }
}