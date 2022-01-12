package world.shanya.serialport.discovery

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_discovery.*
import kotlinx.android.synthetic.main.device_cell.view.*
import world.shanya.serialport.R
import world.shanya.serialport.SerialPort
import world.shanya.serialport.connect.SerialPortConnect
import world.shanya.serialport.discovery.SerialPortDiscovery.pairedDevicesListBD
import world.shanya.serialport.discovery.SerialPortDiscovery.unPairedDevicesListBD
import world.shanya.serialport.strings.SerialPortToast
import world.shanya.serialport.tools.LogUtil
import world.shanya.serialport.tools.ToastUtil

/**
 * DiscoveryActivity 搜索页面Activity
 * @Author Shanya
 * @Date 2021-5-28
 * @Version 3.1.0
 */
class DiscoveryActivity : AppCompatActivity() {

    //连接进度对话框
    private lateinit var connectProcessDialog: Dialog

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

        LogUtil.log("内置搜索页面创建")

        //检查是否打开蓝牙
        if (!SerialPort.bluetoothAdapter.isEnabled) {
            SerialPort.bluetoothAdapter.enable()
        }

        //初始化连接进度对话框
        connectProcessDialog = Dialog(this)
        connectProcessDialog.setContentView(R.layout.progress_dialog_layout)
        connectProcessDialog.setCancelable(false)



        //搜索状态监听
        SerialPortDiscovery.discoveryStatusLiveData.observe(this, Observer {
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
            LogUtil.log("获取定位权限用于扫描蓝牙设备")
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
                            LogUtil.log("定位权限获取成功")
                            recyclerViewInit()
                            doDiscovery()
                        } else {
                            LogUtil.log("定位权限获取失败")
                            ToastUtil.toast(this,SerialPortToast.permission)
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
            unpairedDevicesAdapter.setDevice(unPairedDevicesListBD)
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
        connectProcessDialog.dismiss()
        LogUtil.log("内置搜索页面销毁")
    }

    /**
    * 设备列表适配器
    * @Author Shanya
    * @Date 2021-8-13
    * @Version 4.0.3
    */
    inner class DevicesAdapter internal constructor(context: Context, private val pairingStatus: Boolean) :
        RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)
        private var pairedDevices = ArrayList<BluetoothDevice>()
        private var unpairedDevices = ArrayList<BluetoothDevice>()

        inner class DevicesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewDeviceName: TextView = itemView.findViewById(R.id.textViewDeviceName)
            val textViewDeviceAddress: TextView = itemView.findViewById(R.id.textViewDeviceAddress)
            val imageViewDeviceLogo: ImageView = itemView.findViewById(R.id.imageViewDeviceLogo)
            val textViewDeviceType: TextView = itemView.findViewById(R.id.textViewDeviceType)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
            val holder = DevicesViewHolder(inflater.inflate(R.layout.device_cell,parent,false))
            holder.itemView.setOnClickListener {
                SerialPortDiscovery.stopLegacyScan(this@DiscoveryActivity)
                SerialPortDiscovery.stopBleScan()
                val device = SerialPort.bluetoothAdapter.getRemoteDevice(it.textViewDeviceAddress.text.toString())
                if (SerialPort.openConnectionTypeDialogFlag) {
                    val connectTypeDialog = AlertDialog.Builder(this@DiscoveryActivity)
                        .setTitle("选择连接方式")
                        .setItems(R.array.connect_string) { dialog, which ->
                            connectProcessDialog.show()
                            if (which == 0) {
                                SerialPort._connectBleDevice(device)
                            }else if (which == 1) {
                                SerialPort._connectLegacyDevice(device)
                            }
                        }
                        .create().show()
                }else{
                    connectProcessDialog.show()
                    SerialPort._connectDevice(device, this@DiscoveryActivity)
                }


            }
            return holder
        }

        override fun getItemCount(): Int {
            return if (pairingStatus)
                pairedDevicesListBD.size
            else
                unPairedDevicesListBD.size
        }


        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
            if (pairingStatus) {
                val current = pairedDevicesListBD[position]
                holder.textViewDeviceName.text = current.name
                holder.textViewDeviceAddress.text = current.address
                when (current.type) {
                    BluetoothDevice.DEVICE_TYPE_UNKNOWN -> {
                        holder.textViewDeviceType.text = "未知类型"
                        holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo)
                    }

                    BluetoothDevice.DEVICE_TYPE_CLASSIC -> {
                        holder.textViewDeviceType.text = "传统类型"
                        holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo_l)
                    }
                    BluetoothDevice.DEVICE_TYPE_LE -> {
                        holder.textViewDeviceType.text = "BLE类型"
                        holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo_b)
                    }
                    BluetoothDevice.DEVICE_TYPE_DUAL -> {
                        holder.textViewDeviceType.text = "传统和BLE双重类型"
                        holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo_l_b)
                    }
                }
            }
            else {
                val current = unPairedDevicesListBD[position]
                holder.textViewDeviceName.text = current.name
                holder.textViewDeviceAddress.text = current.address
                when (current.type) {
                    BluetoothDevice.DEVICE_TYPE_UNKNOWN -> {
                        holder.textViewDeviceType.text = "未知类型"
                        holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo)
                    }

                    BluetoothDevice.DEVICE_TYPE_CLASSIC -> {
                        holder.textViewDeviceType.text = "传统类型"
                        holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo_l)
                    }
                    BluetoothDevice.DEVICE_TYPE_LE -> {
                        holder.textViewDeviceType.text = "BLE类型"
                        holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo_b)
                    }
                    BluetoothDevice.DEVICE_TYPE_DUAL -> {
                        holder.textViewDeviceType.text = "传统和BLE双重类型"
                        holder.imageViewDeviceLogo.setImageResource(R.mipmap.device_logo_l_b)
                    }
                }
            }
        }

        internal fun setDevice(devices: ArrayList<BluetoothDevice>) {
            if (pairingStatus) {
                pairedDevices = devices
            } else {
                unpairedDevices = devices
            }
            notifyDataSetChanged()
        }
    }
}