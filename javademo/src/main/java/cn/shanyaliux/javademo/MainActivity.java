package cn.shanyaliux.javademo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import world.shanya.serialport.SerialPort;
import world.shanya.serialport.SerialPortBuilder;

@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StringBuilder stringBuilder = new StringBuilder();
        TextView textViewReceived = findViewById(R.id.textViewReceiced);
        TextView textViewConnectInfo = findViewById(R.id.textViewConnectInfo);
        Button buttonConnect = findViewById(R.id.buttonConnect);
        Button buttonDisconnect = findViewById(R.id.buttonDisconnect);
        EditText editTextSendData = findViewById(R.id.editTextTextSend);
        Button buttonSend = findViewById(R.id.buttonSend);

        SerialPort serialPort = SerialPortBuilder.INSTANCE
                .setReceivedDataCallback((Function1<String, Unit>) s -> {
                    runOnUiThread(() -> {
                        stringBuilder.append(s);
                        textViewReceived.setText(stringBuilder.toString());
                    });
                    return null;
                })
                .setConnectionStatusCallback((aBoolean, bluetoothDevice) -> {
                    runOnUiThread(() -> {
                        String info;
                        if (aBoolean) {
                            info = "设备名称:\t" + bluetoothDevice.getName() +
                                    "\n设备地址:\t" + bluetoothDevice.getAddress() +
                                    "\n 设备类型:\t" + bluetoothDevice.getType();

                        }else{
                            info = "";
                        }
                        textViewConnectInfo.setText(info);
                    });
                    return null;
                })
                .build(this);

        buttonConnect.setOnClickListener((v) -> serialPort.openDiscoveryActivity());

        buttonDisconnect.setOnClickListener((v)-> serialPort.disconnect());

        buttonSend.setOnClickListener((v-> serialPort.sendData(editTextSendData.getText().toString())));

    }
}