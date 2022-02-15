/*
 * 项目名：SerialPortSample
 * 模块名：SerialPortSample.serialport
 * 类名：SerialPortConfig
 * 作者：Shanya
 * 日期：2022/2/12 下午4:57
 * Copyright  (c)  2021  https://shanyaliux.cn
 */

package world.shanya.serialport;

/**
 * SerialPortConfig 配置信息类
 * @Author Shanya
 * @Date 2022-2-12
 * @Version 4.1.5
 */
public class SerialPortConfig {
    public Boolean debug = false;
    public String UUID_LEGACY = "00001101-0000-1000-8000-00805F9B34FB";
    public String UUID_BLE = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public String UUID_BLE_READ = "";
    public String UUID_BLE_SEND = "";
    public Boolean autoConnect = false;
    public Boolean autoReconnect = false;
    public int reconnectAtIntervals = 10000;
    public Boolean autoOpenDiscoveryActivity = false;
    public Boolean autoHexStringToString = false;
    public int readDataType = SerialPort.READ_STRING;
    public int sendDataType = SerialPort.SEND_STRING;
    public Boolean ignoreNoNameDevice = false;
    public Boolean openConnectionTypeDialogFlag = false;
}
