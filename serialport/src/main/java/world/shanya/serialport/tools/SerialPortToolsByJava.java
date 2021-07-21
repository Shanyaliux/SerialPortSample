package world.shanya.serialport.tools;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * SerialPortToolsByJava BLE设备发消息工具类
 * @Author Shanya
 * @Date 2021-7-21
 * @version 4.0.0
 */
public class SerialPortToolsByJava {

    /**
     * dataSeparate 数据分段
     * @param len 数据长度
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    static int[] dataSeparate(int len) {
        int[] lens = new int[2];
        lens[0] = len / 20;
        lens[1] = len - 20 * lens[0];
        return lens;
    }

    /**
     * bleSendData ble设备发送数据
     * @param data 数据
     * @param gatt gatt
     * @param gattCharacteristic gattCharacteristic
     * @Author Shanya
     * @Date 2021-7-21
     * @Version 4.0.0
     */
    public static void bleSendData(BluetoothGatt gatt, BluetoothGattCharacteristic gattCharacteristic, String data) {
        byte[] buff = data.getBytes();
        int len = buff.length;
        int[] lens = dataSeparate(len);
        for (int i = 0; i < lens[0]; i++) {
            String str = new String(buff, 20 * i, 20);
            gattCharacteristic.setValue(str);
        }
        if (lens[1] != 0) {
            String str = new String(buff, 20 * lens[0], lens[1]);
            gattCharacteristic.setValue(str);
            gatt.writeCharacteristic(gattCharacteristic);
        }
    }
}
