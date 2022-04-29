# 快速上手 (Kotlin)

### 构建SerialPort实例

```Kotlin
val serialPort = SerialPortBuilder.build(this)
```
### 搜索设备

使用方法 `doDiscovery(context)` 搜索设备：
```kotlin
serialPort.doDiscovery(this)
```

使用方法 `getPairedDevicesListBD()` 和 `getUnPairedDevicesListBD()` 获取搜索结果：


```kotlin
serialPort.getPairedDevicesListBD()		//获取已配对设备列表
serialPort.getUnPairedDevicesListBD()	//获取未配对设备列表
```
### 连接设备

想要成功的连接设备，并且完成通信，设置正确的UUID是必不可少的一步。

#### 设置传统设备UUID

使用 `SerialPort` 的静态方法 `setLegacyUUID(uuid)` 设置传统设备的UUID：


```kotlin
SerialPort.setLegacyUUID("00001101-0000-1000-8000-00805F9B34FB")
```

传统设备**一般**情况下，可以不用设置UUID，使用默认的即可。


#### 设置BLE设备UUID

使用 `SerialPort` 的静态方法 `setBleUUID(uuid)` 设置BLE设备的UUID：


```kotlin
SerialPort.setBleUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
```


BLE设备大多数情况下都需要设置UUID，具体的UUID可以查询手册或咨询卖家。

除此之外，也可以使用方法 `printPossibleBleUUID()` 打印出可行的UUID，自行选择尝试：

```kotlin
serialPort.printPossibleBleUUID()
```


#### 建立连接

使用方法 `openDiscoveryActivity()` 打开内置的搜索页面选择设备进行连接：


```kotlin
serialPort.openDiscoveryActivity()
```

**不想使用内置的搜索页面怎么办？**

可以设置自定义的搜索页面或者直接使用设备地址进行连接。详见[使用自定义的界面](./discovery_connect_kotlin.html#id3)


### 接收消息

使用方法 `setReceivedDataCallback(receivedDataCallback)`  设置一个接收消息监听器：

```kotlin
serialPort.setReceivedDataCallback { data ->

        }
```

除此之外，你还可以在构建实例时配置监听器：

```kotlin
val serialPort = SerialPortBuilder
            .setReceivedDataCallback { data ->

            }
            .build(this)
```

### 发送消息

使用方法 `sendData(data)` 发送消息：

```kotlin
serialPort.sendData("Hello World")
```

**至此，你已经可以快速的开发一款能够完成基本收发数据的串口应用了。当然，`SerialPort` 还有着更多的功能，请继续阅读说明文档。**


