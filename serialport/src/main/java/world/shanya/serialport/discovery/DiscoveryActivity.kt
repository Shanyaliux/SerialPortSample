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
import android.widget.ImageView
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
import world.shanya.serialport.SerialPort.Companion.logUtil
import world.shanya.serialport.SerialPort.Companion.pairedDevicesList
import world.shanya.serialport.SerialPort.Companion.unPairedDevicesList

/**
 * DiscoveryActivity 搜索页面Activity
 * @Author Shanya
 * @Date 2021-5-28
 * @Version 3.1.0
 */
class DiscoveryActivity : AppCompatActivity() {

    //连接进度对话框
    private lateinit var dialog: Dialog

    /**
    * Activity创建
    * @param savedInstanceState
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discovery)

        logUtil.log("内置搜索页面","创建")

        //检查是否打开蓝牙
        if (!SerialPort.bluetoothAdapter.isEnabled) {
            SerialPort.bluetoothAdapter.enable()
        }

        //初始化连接进度对话框
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_dialog_layout)
        dialog.setCancelable(false)

        //搜索状态监听
        SerialPort.discoveryStatusLiveData.observe(this, Observer {
            if (it) {
                swipeRedreshLayout.isRefreshing = true
                title = "正在搜索……"
            } else {
                swipeRedreshLayout.isRefreshing = false
                title = "请选择一个设备连接"
            }
        })

        //连接监听
        SerialPort.setConnectListener {
            finish()
        }

        //下拉搜索监听
        swipeRedreshLayout.setOnRefreshListener {
            doDiscovery()
        }

        //申请定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            logUtil.log("扫描蓝牙设备","获取定位权限")
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
                            logUtil.log("扫描蓝牙设备","定位权限获取成功")
                            recyclerViewInit()
                            doDiscovery()
                        } else {
                            logUtil.log("扫描蓝牙设备","定位权限获取失败")
                            Toast.makeText(this, "请先开启位置权限", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
            }
        }

        //设备列表初始化
        recyclerViewInit()
        //开始搜索
        doDiscovery()

    }

    /**
    * 开始执行设备搜索
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
    private fun doDiscovery() {
        title = "正在搜索……"
        SerialPortDiscovery.startLegacyScan(this)
        SerialPortDiscovery.startBleScan()
    }

    /**
    * 设备列表初始化
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
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
            unpairedDevicesAdapter.setDevice(unPairedDevicesList)
        }
    }

    /**
    * 创建右上角菜单
    * @param menu
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.discovery_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
    * 右上角菜单项监听
    * @param item
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.discovery_menu_item -> {
                doDiscovery()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
    * Activity销毁
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
    override fun onDestroy() {
        super.onDestroy()
        SerialPortDiscovery.stopLegacyScan(this)
        SerialPortDiscovery.stopBleScan()
        dialog.dismiss()
        logUtil.log("内置搜索页面","销毁")
    }

    /**
    * 设备列表适配器
    * @Author Shanya
    * @Date 2021/5/28
    * @Version 3.1.0
    */
    inner class DevicesAdapter internal constructor(context: Context, private val pairingStatus: Boolean) :
        RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var pairedDevices = ArrayList<Device>()
        private var unpairedDevices = ArrayList<Device>()

        inner class DevicesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewDeviceName: TextView = itemView.findViewById(R.id.textViewDeviceName)
            val textViewDeviceAddress: TextView = itemView.findViewById(R.id.textViewDeviceAddress)
            val imageViewDeviceLogo: ImageView = itemView.findViewById(R.id.imageViewDeviceLogo)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
            val holder = DevicesViewHolder(inflater.inflate(R.layout.device_cell,parent,false))
            holder.itemView.setOnClickListener {
                dialog.show()
                SerialPort.bluetoothAdapter.cancelDiscovery()
                if (pairedDevicesList.contains(
                        Device(
                            it.textViewDeviceName.text.toString(),
                            it.textViewDeviceAddress.text.toString(), false
                        )
                    )
                ) {
                    SerialPort._connectDevice(
                        Device(
                            it.textViewDeviceName.text.toString(),
                            it.textViewDeviceAddress.text.toString(), false
                        )
                    )
                } else {
                    SerialPort.connectBle(it.textViewDeviceAddress.text.toString())
                }
                if (unPairedDevicesList.contains(
                        Device(
                            it.textViewDeviceName.text.toString(),
                            it.textViewDeviceAddress.text.toString(), false
                        )
                    )
                ) {
                    Device(
                        it.textViewDeviceName.text.toString(),
                        it.textViewDeviceAddress.text.toString(), false
                    )
                } else {
                    SerialPort.connectBle(it.textViewDeviceAddress.text.toString())
                }

            }
            return holder
        }

        override fun getItemCount(): Int {
            return if (pairingStatus)
                pairedDevicesList.size
            else
                unPairedDevicesList.size
        }

        override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
            if (pairingStatus) {
                val current = pairedDevicesList[position]
                holder.textViewDeviceName.text = current.name
                holder.textViewDeviceAddress.text = current.address
                if (current.isBle) {
                    holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo_ble)
                } else {
                    holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo)
                }
            }
            else {
                val current = unPairedDevicesList[position]
                holder.textViewDeviceName.text = current.name
                holder.textViewDeviceAddress.text = current.address
                if (current.isBle) {
                    holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo_ble)
                } else {
                    holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo)
                }
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