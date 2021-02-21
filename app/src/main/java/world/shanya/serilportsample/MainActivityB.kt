package world.shanya.serilportsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import world.shanya.serialport.SerialPortBuilder

class MainActivityB : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_b)

        val serialPort = SerialPortBuilder.isDebug(true).build(this)
    }
}