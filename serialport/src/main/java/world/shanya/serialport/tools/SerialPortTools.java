package world.shanya.serialport.tools;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import world.shanya.serialport.SerialPort;

/**
 * SerialPortToolsByJava BLE设备发消息工具类
 * @Author Shanya
 * @Date 2021-7-21
 * @version 4.0.0
 */
@SuppressLint("MissingPermission")
public class SerialPortTools {

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
        new Thread(() -> {
            try {
                if (gattCharacteristic != null) {
                    LogUtil.INSTANCE.log("BLE设备发送数据", data);
                    byte[] buff = SerialPortTools.string2bytes(data, "GBK");
                    int len = buff.length;
                    int[] lens = dataSeparate(len);
                    for (int i = 0; i < lens[0]; i++) {
                        String str = new String(buff, 20 * i, 20);
                        gattCharacteristic.setValue(str);
                        gatt.writeCharacteristic(gattCharacteristic);
                        try {
                            Thread.sleep(SerialPort.Companion.getBleSendSleep());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (lens[1] != 0) {
                        String str = new String(buff, 20 * lens[0], lens[1]);
                        gattCharacteristic.setValue(str);
                        gatt.writeCharacteristic(gattCharacteristic);
                    }
                } else {
                    Log.e("SerialPort", "BLE接收UUID不正确，请检查！");
                    throw new NullPointerException("BLE接收UUID不正确，请检查！");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public static void bleSendData(BluetoothGatt gatt, BluetoothGattCharacteristic gattCharacteristic, byte[] bytes) {
        new Thread(() -> {
            try {
                if (gattCharacteristic != null) {
                    LogUtil.INSTANCE.log("BLE设备发送byte数据", "");
                    int len = bytes.length;
                    int[] lens = dataSeparate(len);
                    for (int i = 0; i < lens[0]; i++) {
                        byte[] bytesTemp = Arrays.copyOfRange(bytes, 20 * i, 20 * (i + 1));
                        gattCharacteristic.setValue(bytesTemp);
                        gatt.writeCharacteristic(gattCharacteristic);
                        try {
                            Thread.sleep(SerialPort.Companion.getBleSendSleep());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (lens[1] != 0) {
                        byte[] bytesTemp = Arrays.copyOfRange(bytes, 20 * lens[0], 20 * lens[0] + lens[1]);
                        gattCharacteristic.setValue(bytesTemp);
                        gatt.writeCharacteristic(gattCharacteristic);
                    }
                } else {
                    Log.e("SerialPort", "BLE接收UUID不正确，请检查！");
                    throw new NullPointerException("BLE接收UUID不正确，请检查！");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 字节数组按要求的编码格式转换成字符串
     * @param bytes 带转换的字节数组
     * @param charsetName 要求的编码格式
     * @return 转换成功的字符串
     * @Author Shanya
     * @Date 2021-12-10
     * @Version 4.1.2
     */
    public static String bytes2string(byte[] bytes, String charsetName) {
        String s = null;
        try {
            s = new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 字符串按要求的编码格式转换成字节数组
     * @param string 带转换的字符串
     * @param charsetName 要求的编码格式
     * @return 转换成功的字节数组
     * @Author Shanya
     * @Date 2021-12-10
     * @Version 4.1.2
     */
    public static byte[] string2bytes(String string, String charsetName) {
        byte[] bytes = null;
        try {
            bytes = string.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static void setDiscoverableTimeout() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);
            setDiscoverableTimeout.invoke(adapter, 0);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Bluetooth", "setDiscoverableTimeout failure:" + e.getMessage());
        }
    }

    public static void closeDiscoverableTimeout() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);
            setDiscoverableTimeout.invoke(adapter, 1);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
