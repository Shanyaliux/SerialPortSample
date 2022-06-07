# 收发数据

## 设置数据格式

使用方法 `setReadDataType(type)` 和 `setSendDataType(type)` 来设置手法数据的格式：

### 设置接收消息格式

```kotlin
//SerialPort.READ_HEX 十六进制
//SerialPort.READ_STRING 字符串
//不设置则默认字符串形式
serialPort.setReadDataType(SerialPort.READ_HEX)
```

除此之外，你还可以在构建实例时设置接收数据格式：

```kotlin
//SerialPort.READ_HEX 十六进制
//SerialPort.READ_STRING 字符串
//不设置则默认字符串形式
val serialPort = SerialPortBuilder
            .setReadDataType(SerialPort.READ_HEX)
            .build(this)
```

### 设置发送数据格式

```kotlin
//SerialPort.SEND_HEX 十六进制
//SerialPort.SEND_STRING 字符串
//不设置则默认字符串形式
serialPort.setSendDataType(SerialPort.SEND_HEX )
```

除此之外，你还可以在构建实例时设置接收数据格式：

```kotlin
//SerialPort.SEND_HEX 十六进制
//SerialPort.SEND_STRING 字符串
//不设置则默认字符串形式
val serialPort = SerialPortBuilder
            .setSendDataType(SerialPort.SEND_HEX)
            .build(this)
```

目前针对于BLE设备的数据收发暂不支持设置格式，仅支持字符串格式。如果实在需要十六进制的数据格式，暂时可以参考传统设备的处理方式自行实现。

参考代码链接：[HexStringToString](https://gitee.com/Shanya/SerialPortSample/blob/master/serialport/src/main/java/world/shanya/serialport/tools/SerialPortToolsByKotlin.kt#L112)、[StringToHex](https://gitee.com/Shanya/SerialPortSample/blob/master/serialport/src/main/java/world/shanya/serialport/tools/SerialPortToolsByKotlin.kt#L199)

## 接收消息

### 字符串 和 十六进制

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

### 字节数组

在接收消息的时候，也可以选择获取**字节数组**，方法如下：

```kotlin
serialPort.setReceivedBytesCallback { bytes ->

        }
```

除此之外，你还可以在构建实例时配置监听器：

```kotlin
val serialPort = SerialPortBuilder
            .setReceivedBytesCallback { bytes ->

            }
            .build(this)
```

## 发送消息

使用方法 `sendData(data)` 发送消息：

### 字符串

```kotlin
serialPort.sendData("Hello World")
```

### 十六进制

```kotlin
serialPort.sendData("0C FF")
```

所有的十六进制应为**两位**，不足两位的前方补0，不区分大小写。

### BLE设备发送字节数组
目前BLE设备支持直接发送字节数组

```kotlin
serialPort.sendData(bytes)
```
