package world.shanya.serialport.discovery

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_discovery.*
import kotlinx.android.synthetic.main.device_cell.view.*
import world.shanya.serialport.R
import world.shanya.serialport.SerialPort
import world.shanya.serialport.broadcast.DiscoveryBroadcastReceiver

/**
 * DiscoveryActivity 搜索页面Activity
 * @Author Shanya
 * @Date 2021-3-16
 * @Version 3.0.0
 */
class DiscoveryActivity : AppCompatActivity() {

    private val discoveryBroadcastReceiver = DiscoveryBroadcastReceiver()

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discovery)

        if (!SerialPort.bluetoothAdapter.isEnabled) {
            SerialPort.bluetoothAdapter.enable()
        }

        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_dialog_layout)
        dialog.setCancelable(false)

        SerialPort.discoveryStatusLiveData.observe(this, Observer {
            if (it) {
                swipeRedreshLayout.isRefreshing = true
            } else {
                swipeRedreshLayout.isRefreshing = false
                title = "请选择一个设备连接"
            }
        })

        SerialPort.setConnectListener {
            finish()
        }

        swipeRedreshLayout.setOnRefreshListener {
            doDiscovery()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SerialPort.logUtil.log("Discovery","Getting permission")
            if (!PermissionX.isGranted(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                PermissionX.init(this)
                    .permissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    .explainReasonBeforeRequest()
                    .onExplainRequestReason { scope, deniedList ->
                        val message = "需要您同意位置权限用于搜索设备，否则搜索功能将不可用。"
                        scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
                    }
                    .onForwardToSettings { scope, deniedList ->
                        val message = "需要您去设置页面同意位置权限用于搜索设备，否则搜索功能将不可用。"
                        scope.showForwardToSettingsDialog(deniedList, message, "确定", "取消")
                    }
                    .request { allGranted, _, _ ->
                        @Suppress("ControlFlowWithEmptyBody")
                        if (allGranted) {
                            SerialPort.logUtil.log("Discovery","Getting permission succeeded")
                            registerDiscoveryReceiver()
                            recyclerViewInit()
                            doDiscovery()
                        } else {
                            SerialPort.logUtil.log("Discovery","Getting permission failed")
                            Toast.makeText(this, "请先开启位置权限", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
            }
        }

        registerDiscoveryReceiver()
        recyclerViewInit()
        doDiscovery()


    }

    private fun registerDiscoveryReceiver() {
        SerialPort.logUtil.log("Discovery","RegisterReceiver")

        registerReceiver(discoveryBroadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        registerReceiver(discoveryBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
    }

    private fun doDiscovery() {

        SerialPort.logUtil.log("Discovery","Get paired devices")

        title = "正在搜索……"

        val pairedDevices:Set<BluetoothDevice> = SerialPort.bluetoothAdapter.bondedDevices
        if (pairedDevices.isNotEmpty()){
            SerialPort.pairedDevicesList.clear()
            for (device in pairedDevices){
                SerialPort.pairedDevicesList.add(Device(device.name?:"unknown",device.address))
            }
        }

        SerialPort.logUtil.log("Discovery","Start discovery")

        SerialPort.unPairedDevicesList.clear()

        SerialPort.bluetoothAdapter.startDiscovery()
    }

    private fun recyclerViewInit() {
        val pairedDevicesAdapter = DevicesAdapter(this, true)
        val unpairedDevicesAdapter = DevicesAdapter(this,false)

        recyclerViewPaired.apply {
            adapter = pairedDevicesAdapter
            layoutManager = LinearLayoutManager(this@DiscoveryActivity)
            addItemDecoration(
                DividerItemDecoration(this@DiscoveryActivity,DividerItemDecoration.VERTICAL)
            )
        }

        recyclerViewUnpaired.apply {
            adapter = unpairedDevicesAdapter
            layoutManager = LinearLayoutManager(this@DiscoveryActivity)
            addItemDecoration(
                DividerItemDecoration(this@DiscoveryActivity,DividerItemDecoration.VERTICAL)
            )
        }

        SerialPort.setFindDeviceListener {
            unpairedDevicesAdapter.setDevice(SerialPort.unPairedDevicesList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.discovery_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.discovery_menu_item -> {
                doDiscovery()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        SerialPort.bluetoothAdapter.cancelDiscovery()
        unregisterReceiver(discoveryBroadcastReceiver)
        dialog.dismiss()
        SerialPort.logUtil.log("DiscoveryActivity","onDestroy")
    }

    inner class DevicesAdapter internal constructor(context: Context, private val pairingStatus: Boolean) :
        RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var pairedDevices = ArrayList<Device>()
        private var unpairedDevices = ArrayList<Device>()

        inner class DevicesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewDeviceName: TextView = itemView.findViewById(R.id.textViewDeviceName)
            val textViewDeviceAddress: TextView = itemView.findViewById(R.id.textViewDeviceAddress)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
            val holder = DevicesViewHolder(inflater.inflate(R.layout.device_cell,parent,false))
            holder.itemView.setOnClickListener {
                dialog.show()
                SerialPort.bluetoothAdapter.cancelDiscovery()
                SerialPort._connectDevice(Device(
                    it.textViewDeviceName.text.toString(),
                    it.textViewDeviceAddress.text.toString()
                ))
            }
            return holder
        }

        override fun getItemCount(): Int {
            return if (pairingStatus)
                SerialPort.pairedDevicesList.size
            else
                SerialPort.unPairedDevicesList.size
        }

        override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
            if (pairingStatus) {
                val current = SerialPort.pairedDevicesList[position]
                holder.textViewDeviceName.text = current.name
                holder.textViewDeviceAddress.text = current.address
            }
            else {
                val current = SerialPort.unPairedDevicesList[position]
                holder.textViewDeviceName.text = current.name
                holder.textViewDeviceAddress.text = current.address
            }
        }

        internal fun setDevice(devices: ArrayList<Device>) {
            if (pairingStatus) {
                pairedDevices = devices
            } else {
                unpairedDevices = devices
            }
            notifyDataSetChanged()
        }
    }
}