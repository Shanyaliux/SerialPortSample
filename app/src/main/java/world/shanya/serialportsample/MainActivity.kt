package world.shanya.serialportsample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import world.shanya.serialport.SerialPort
import world.shanya.serialport.SerialPortBuilder
import world.shanya.serialportsample.pagers.KeyFragment
import world.shanya.serialportsample.pagers.MessageFragment
import world.shanya.serialportsample.utils.CheckUpdate
import world.shanya.serialportsample.utils.SettingSPTools
import java.security.SecureRandom


class MainActivity : AppCompatActivity() {

    private var serialPort:SerialPort ?= null
    private val checkUpdate = CheckUpdate(this)
    private var toolMenu: Menu ?= null
    private val myViewModel: MyViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //启动时检查更新
        checkUpdate.check()

        viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int) =
                when (position) {
                    0 -> MessageFragment()
                    else -> KeyFragment()
                }
        }

        TabLayoutMediator(tabLayout,viewPager2){ _, _ -> }.attach()

        val config = SettingSPTools.getSerialPortConfig(this)

        serialPort = SerialPortBuilder
            .setConfig(config)
            .isDebug(true)
            .setReceivedDataCallback {
                myViewModel.receivedStringBuilder.append(it)
                myViewModel.receivedLiveData.value = myViewModel.receivedStringBuilder
            }
            .setConnectionStatusCallback { status, bluetoothDevice ->
                MainScope().launch {
                    if (!status) {
                        //断开连接
                        toolMenu?.let {
                            val menuItemConnect = it.findItem(R.id.menu_connect)
                            menuItemConnect.title = "连接"
                            myViewModel.deviceInfoLiveData.value =
                                DeviceInfo(false,"", "", "")
                        }
                    } else {
                        //连接成功
                        toolMenu?.let {
                            val menuItemConnect = it.findItem(R.id.menu_connect)
                            menuItemConnect.title = "断开"
                            myViewModel.deviceInfoLiveData.value =
                                DeviceInfo(true,
                                    bluetoothDevice?.name ?: "",
                                    bluetoothDevice?.address ?: "",
                                    bluetoothDevice?.type.toString()
                                )
                        }
                    }
                }
            }
//            .setBleCanWorkCallback {
//                val ByteArray = ByteArray(21)
//                SecureRandom().nextBytes(ByteArray)
//
//                for (i in ByteArray) {
//                    Log.d("SerialPort", "onCreate: " + i)
//
//                }
//                SerialPortBuilder.sendData(ByteArray)
//            }
            .build(this)
    }

    override fun onResume() {
        super.onResume()
        SerialPortBuilder.setConfig(SettingSPTools.getSerialPortConfig(this))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        toolMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_about ->
                startActivity(Intent(this, AboutActivity::class.java))
            R.id.menu_setting ->
                startActivity(Intent(this, SettingActivity::class.java))
            R.id.menu_connect -> {
                if (item.title == "连接") {
                    serialPort?.openDiscoveryActivity()
                } else {
                    serialPort?.disconnect()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}