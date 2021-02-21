package world.shanya.serilportsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import world.shanya.serialport.SerialPort

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SerialPort.get()
    }
}