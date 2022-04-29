# 配置

## 调试模式

在调试程序的时候，我们可以打开调试模式，这样就会打印各式各样的日志信息，在正式发布APP时关掉此开关即可减少资源的开销。设置方式如下：

```kotlin
val serialPort = SerialPortBuilder
            .isDebug(true)
            .build(this)
```

## 自动重连

### 启动时重连

开启此功能后，会在构建实例的时候执行一次自动重连，重连对象为上一次**成功连接**的设备。设置方式如下：

```kotlin
val serialPort = SerialPortBuilder
            .autoConnect(true)
            .build(this)
```

### 间隔自动重连

开启此功能后，会间隔一段时间自动重连一次（时间可自行设置），重连对象为上一次**成功连接**的设备。设置方式如下：

```kotlin
val serialPort = SerialPortBuilder
			//第二个参数为间隔时间，若不指定则为默认 10000Ms
            .setAutoReconnectAtIntervals(true, 10000)
            .build(this)
```

## 忽略无名设备

开启此功能后，搜索设备时就会自动忽略设备名为空的设备。设置方式如下：

```kotlin
val serialPort = SerialPortBuilder
            .isIgnoreNoNameDevice(true)
            .build(this)
```

**部分蓝牙设备可能会在第一次连接出现设备名为空的情况，请视情况而定开启此功能。**

## 自动打开搜索界面

开启此功能后，在发送数据时，若发现未连接设备则会自动打开内置的搜索页面。设置方式如下：

```kotlin
val serialPort = SerialPortBuilder
            .autoOpenDiscoveryActivity(true)
            .build(this)
```

## 十六进制数据自动转换

开启此功能后，在收到的数据为十六进制时，会自动将其转换为字符串。设置方式如下：

```kotlin
val serialPort = SerialPortBuilder
            .autoHexStringToString(true)
            .build(this)
```

当然，你也可以使用方法 `hexStringToString(hexString)` 手动进行转换：

```kotlin
string = serialPort.hexStringToString(hexString)
```

## 内置搜索页面选择连接方式
开启此功能后在内置页面点击设备进行连接的时候，可以手动选择连接方式。但请注意若你的设备不支持你所选的连接方式，则连接不会成功。

```kotlin
val serialPort = SerialPortBuilder
            .setOpenConnectionTypeDialogFlag(true)
            .build(this)
```

## 配置器

配置可以将上述的多种配置一次性传入`SerialPortBuilder`。

```kotlin
val config = SerialPortConfig()
val serialPort = SerialPortBuilder
            .setConfig(config)
            .build(this)
```

其中配置器可设置的参数如下表所示 (加粗的表示默认值)：

|           参数名称           |                        值                         |
| :--------------------------: | :-----------------------------------------------: |
|            debug             |              (bool) true / **false**              |
|         UUID_LEGACY          | (string) **00001101-0000-1000-8000-00805F9B34FB** |
|           UUID_BLE           | (string) **00001101-0000-1000-8000-00805F9B34FB** |
|        UUID_BLE_READ         |                  (string) **无**                  |
|        UUID_BLE_SEND         |                  (string) **无**                  |
|         autoConnect          |              (bool) true / **false**              |
|        autoReconnect         |              (bool) true / **false**              |
|     reconnectAtIntervals     |                  (int) **10000**                  |
|  autoOpenDiscoveryActivity   |              (bool) true / **false**              |
|    autoHexStringToString     |              (bool) true / **false**              |
|         readDataType         | **SerialPort.READ_STRING** / SerialPort.READ_HEX  |
|         sendDataType         | **SerialPort.SEND_STRING** / SerialPort.SEND_HEX  |
|      ignoreNoNameDevice      |              (bool) true / **false**              |
| openConnectionTypeDialogFlag |              (bool) true / **false**              |

**其中关于UUID的设置注意事项参考: [ble设备设置UUID](./discovery_connect_kotlin.html#ble)**

## 设置搜索时长

使用此方法配置搜索设备时长：

```kotlin
//参数为时间，单位毫秒
val serialPort = SerialPortBuilder
            .setDiscoveryTime(10000)
            .build(this)
```

