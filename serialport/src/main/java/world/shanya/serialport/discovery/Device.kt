package world.shanya.serialport.discovery

/**
 * Device 设备
 * @Author Shanya
 * @Date 2021-5-28
 * @Version 3.1.0
 */
data class Device(
    val name:String,
    val address:String,
    val type:Int = 1
)