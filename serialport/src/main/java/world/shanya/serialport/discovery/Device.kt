package world.shanya.serialport.discovery

/**
 * Device 设备
 * @Author Shanya
 * @Date 2021-7-21
 * @Version 4.0.0
 */
@Deprecated("该类在4.0.0版本被弃用,将直接使用官方的BluetoothDevice类代替")
data class Device(
    val name:String,
    val address:String,
    val type:Int = 0
)