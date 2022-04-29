# 配置

## 调试模式

在调试程序的时候，我们可以打开调试模式，这样就会打印各式各样的日志信息，在正式发布APP时关掉此开关即可减少资源的开销。设置方式如下：

:::: code-group

::: code-group-item Kotlin

```kotlin
val serialPort = SerialPortBuilder
            .isDebug(true)
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .isDebug(true)
                .build(this);
```

:::

::::

## 自动重连

### 启动时重连

开启此功能后，会在构建实例的时候执行一次自动重连，重连对象为上一次**成功连接**的设备。设置方式如下：

:::: code-group

::: code-group-item Kotlin

```kotlin
val serialPort = SerialPortBuilder
            .autoConnect(true)
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .autoConnect(true)
                .build(this);
```

:::

::::

### 间隔自动重连

开启此功能后，会间隔一段时间自动重连一次（时间可自行设置），重连对象为上一次**成功连接**的设备。设置方式如下：

:::: code-group

::: code-group-item Kotlin

```kotlin
val serialPort = SerialPortBuilder
			//第二个参数为间隔时间，若不指定则为默认 10000Ms
            .setAutoReconnectAtIntervals(true, 10000)
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
    			//第二个参数为间隔时间，若不指定则为默认 10000Ms
                .setAutoReconnectAtIntervals(true, 10000)
                .build(this);
```

:::

::::

## 忽略无名设备

开启此功能后，搜索设备时就会自动忽略设备名为空的设备。设置方式如下：

:::: code-group

::: code-group-item Kotlin

```kotlin
val serialPort = SerialPortBuilder
            .isIgnoreNoNameDevice(true)
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .isIgnoreNoNameDevice(true)
                .build(this);
```

:::

::::

::: warning 注意

部分蓝牙设备可能会在第一次连接出现设备名为空的情况，请示情况而定开启此功能。

:::

## 自动打开搜索界面

开启此功能后，在发送数据时，若发现未连接设备则会自动打开内置的搜索页面。设置方式如下：

:::: code-group

::: code-group-item Kotlin

```kotlin
val serialPort = SerialPortBuilder
            .autoOpenDiscoveryActivity(true)
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .autoOpenDiscoveryActivity(true)
                .build(this);
```

:::

::::

## 十六进制数据自动转换

开启此功能后，在收到的数据为十六进制时，会自动将其转换为字符串。设置方式如下：

:::: code-group

::: code-group-item Kotlin

```kotlin
val serialPort = SerialPortBuilder
            .autoHexStringToString(true)
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .autoHexStringToString(true)
                .build(this);
```

:::

::::

::: tip 提示

当然，你也可以使用方法 `hexStringToString(hexString)` 手动进行转换：

```kotlin
string = serialPort.hexStringToString(hexString)
```

:::

## [内置搜索页面选择连接方式]()
开启此功能后在内置页面点击设备进行连接的时候，可以手动选择连接方式。但请注意若你的设备不支持你所选的连接方式，则连接不会成功。

:::: code-group

::: code-group-item Kotlin

```kotlin
val serialPort = SerialPortBuilder
            .setOpenConnectionTypeDialogFlag(true)
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .setOpenConnectionTypeDialogFlag(true)
                .build(this);
```

:::

::::

## 配置器

配置可以将上述的多种配置一次性传入`SerialPortBuilder`。

:::: code-group

::: code-group-item Kotlin

```kotlin
val config = SerialPortConfig()
val serialPort = SerialPortBuilder
            .setConfig(config)
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPortConfig config = new SerialPortConfig();
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .setConfig(config)
                .build(this);
```

:::

::::

其中配置器可设置的参数如下表所示：

|           参数名称           |                默认值                |                    可选值                    |
| :--------------------------: | :----------------------------------: | :------------------------------------------: |
|            debug             |                false                 |                 true / false                 |
|         UUID_LEGACY          | 00001101-0000-1000-8000-00805F9B34FB |                                              |
|           UUID_BLE           | 00001101-0000-1000-8000-00805F9B34FB |                                              |
|        UUID_BLE_READ         |                  无                  |                                              |
|        UUID_BLE_SEND         |                  无                  |                                              |
|         autoConnect          |                false                 |                 true / false                 |
|        autoReconnect         |                false                 |                 true / false                 |
|     reconnectAtIntervals     |                10000                 |                                              |
|  autoOpenDiscoveryActivity   |                false                 |                 true / false                 |
|    autoHexStringToString     |                false                 |                 true / false                 |
|         readDataType         |        SerialPort.READ_STRING        | SerialPort.READ_STRING / SerialPort.READ_HEX |
|         sendDataType         |        SerialPort.SEND_STRING        | SerialPort.SEND_STRING / SerialPort.SEND_HEX |
|      ignoreNoNameDevice      |                false                 |                 true / false                 |
| openConnectionTypeDialogFlag |                false                 |                 true / false                 |

::: warning 注意

其中关于UUID的设置注意事项参考: [ble设备设置UUID](/serialport/guide/discovery_connect.html#ble设备)

:::

