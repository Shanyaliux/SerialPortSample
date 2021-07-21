package world.shanya.serialport.tools;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class SerialPortTools {
    static int[] dataSeparate(int len) {
        int[] lens = new int[2];
        lens[0] = len / 20;
        lens[1] = len - 20 * lens[0];
        return lens;
    }

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
