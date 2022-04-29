# Toast提示设置

## 配置方法

```kotlin
//是否显示
SerialPortToast.connectSucceeded.status = true
//提示内容 content 是字符串id
SerialPortToast.connectSucceeded.content = R.string.connectSucceededToast
//显示时长 Toast.LENGTH_SHORT 或 Toast.LENGTH_LONG
SerialPortToast.connectSucceeded.time = Toast.LENGTH_SHORT
```

## 可选配置项

|          项目          |               描述               |                  默认值                   |
| :--------------------: | :------------------------------: | :---------------------------------------: |
|    connectSucceeded    |            连接成功时            |                 连接成功                  |
|     connectFailed      |            连接失败时            |                 连接失败                  |
|       disconnect       |            断开连接时            |                 断开连接                  |
|      connectFirst      |     未连接设备时执行发送数据     |               请先连接设备                |
|    disconnectFirst     |     已连接设备后执行连接操作     |               请先断开连接                |
|       permission       |       询问是否开启定位权限       |             请先开启位置权限              |
|         hexTip         | 发送十六进制时，数据格式不对提示 | 请输入的十六进制数据保持两位，不足前面补0 |
| openBluetoothSucceeded |          打开蓝牙成功时          |               蓝牙打开成功                |
|  openBluetoothFailed   |          打开蓝牙失败时          |               蓝牙打开失败                |