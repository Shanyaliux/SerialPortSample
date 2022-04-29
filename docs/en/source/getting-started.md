# 快速上手

## 安装

编辑Build.gradle文件并添加以下依赖项。

```groovy
repositories {
  google()
  mavenCentral()
}

dependencies {
    implementation 'cn.shanyaliux.serialport:serialport:4.1.7'
}
```

::: details 使用4.1.6及以下版本参考此处

#### 添加 JitPack 仓库

##### 将 JitPack 存储库添加到您的构建文件中

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### 添加依赖

##### 国内仓库

```groovy
dependencies {
    implementation 'com.gitee.Shanya:SerialPortSample:4.1.6' 
}
```

##### 国外仓库

```groovy
dependencies {
    implementation 'com.github.Shanyaliux:SerialPortSample:4.1.6' 
}
```

::: tip 提示

以上两个依赖根据自身情况二选一即可

:::

## 使用

### 构建SerialPort

:::: code-group

::: code-group-item Kotlin

```Kotlin
val serialPort = SerialPortBuilder.build(this)
```

以上方法仅仅是构建了一个最简单的 `SerialPort` 实例，它拥有着以下方法：

- `openDiscoveryActivity()` 打开内置搜索页面
- `openDiscoveryActivity(intent: Intent)` 打开自定义搜索页面
- `setReadDataType(type: Int)` 设置接收数据格式
- `setSendDataType(type: Int)` 设置发送数据格式
- `connectLegacyDevice(address: String)` 连接传统蓝牙设备
- `connectBle(address: String)` 连接BLE蓝牙设备
- `connectDevice(address: String)` 连接设备
- `disconnect()` 断开连接
- `setReceivedDataCallback(receivedDataCallback: ReceivedDataCallback)` 收到消息回调
- `setDiscoveryStatusWithTypeCallback(discoveryStatusWithTypeCallback: DiscoveryStatusWithTypeCallback)` 带设备类型的搜索状态回调
- `setDiscoveryStatusCallback(discoveryStatusCallback: DiscoveryStatusCallback)` 搜索状态回调
- `setConnectionStatusCallback(connectionStatusCallback: ConnectionStatusCallback)` 连接状态回调
- `setConnectionResultCallback(connectionResultCallback: ConnectionResultCallback)` 连接结果回调
- `printPossibleBleUUID()` 打印可能BLE设备UUID
- `sendData(data: String)` 发送数据
- `hexStringToString(hexString: String)` 十六进制的字符串转换为传统的字符串
- `getPairedDevicesListBD()` 获取已配对设备列表
- `getUnPairedDevicesListBD()` 获取未配对设备列表
- `doDiscovery(context: Context)` 搜索设备
- `cancelDiscovery(context: Context)` 取消搜索

::: 

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE.build(this);
```

以上方法仅仅是构建了一个最简单的 `SerialPort` 实例，它拥有着以下方法：

- `openDiscoveryActivity()` 打开内置搜索页面
- `openDiscoveryActivity(Intent intent)` 打开自定义搜索页面
- `setReadDataType(Int type)` 设置接收数据格式
- `setSendDataType(Int type)` 设置发送数据格式
- `connectLegacyDevice(String address)` 连接传统蓝牙设备
- `connectBle(String address)` 连接BLE蓝牙设备
- `connectDevice(String address)` 连接设备
- `disconnect()` 断开连接
- `setReceivedDataCallback(ReceivedDataCallback receivedDataCallback)` 收到消息回调
- `setDiscoveryStatusWithTypeCallback(DiscoveryStatusWithTypeCallback discoveryStatusWithTypeCallback)` 带设备类型的搜索状态回调
- `setDiscoveryStatusCallback(DiscoveryStatusCallback discoveryStatusCallback)` 搜索状态回调
- `setConnectionStatusCallback(ConnectionStatusCallback connectionStatusCallback)` 连接状态回调
- `setConnectionResultCallback(ConnectionResultCallback connectionResultCallback)` 连接结果回调
- `printPossibleBleUUID()` 打印可能BLE设备UUID
- `sendData(String data)` 发送数据
- `hexStringToString(String hexString)` 十六进制的字符串转换为传统的字符串
- `getPairedDevicesListBD()` 获取已配对设备列表
- `getUnPairedDevicesListBD()` 获取未配对设备列表
- `doDiscovery(Context context)` 搜索设备
- `cancelDiscovery(Context context)` 取消搜索

:::

::::

### 搜索设备

使用方法 `doDiscovery(context)` 搜索设备：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.doDiscovery(this)
```

:::

::: code-group-item Java

```java
serialPort.doDiscovery(this);
```

:::

::::

使用方法 `getPairedDevicesListBD()` 和 `getUnPairedDevicesListBD()` 获取搜索结果：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.getPairedDevicesListBD()		//获取已配对设备列表
serialPort.getUnPairedDevicesListBD()	//获取未配对设备列表
```

:::

::: code-group-item Java

```java
serialPort.getPairedDevicesListBD();	//获取已配对设备列表
serialPort.getUnPairedDevicesListBD();	//获取未配对设备列表
```

:::

::::

::: warning 注意

如果搜索未结束，则可能获取的未配对设备列表为空或者不全。

:::

### 连接设备

想要成功的连接设备，并且完成通信，设置正确的UUID是必不可少的一步。

#### 设置传统设备UUID

使用 `SerialPort` 的静态方法 `setLegacyUUID(uuid)` 设置传统设备的UUID：

:::: code-group

::: code-group-item Kotlin

```kotlin
SerialPort.setLegacyUUID("00001101-0000-1000-8000-00805F9B34FB")
```

:::

::: code-group-item Java

```java
SerialPort.Companion.setLegacyUUID("00001101-0000-1000-8000-00805F9B34FB");
```

:::

::::

::: warning 注意

传统设备**一般**情况下，可以不用设置UUID，使用默认的即可。

:::

#### 设置BLE设备UUID

使用 `SerialPort` 的静态方法 `setLegacyUUID(uuid)` 设置BLE设备的UUID：

:::: code-group

::: code-group-item Kotlin

```kotlin
SerialPort.setLegacyUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
```

:::

::: code-group-item Java

```java
SerialPort.Companion.setBleUUID("0000ffe1-0000-1000-8000-00805f9b34fb");
```

:::

::::

::: warning 注意

BLE设备大多数情况下都需要设置UUID，具体的UUID可以查询手册或咨询卖家。

除此之外，也可以使用方法 `printPossibleBleUUID()` 打印出可行的UUID，自行选择尝试：

```kotlin
serialPort.printPossibleBleUUID()
```

:::

#### 建立连接

使用方法 `openDiscoveryActivity()` 打开内置的搜索页面选择设备进行连接：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.openDiscoveryActivity()
```

:::

::: code-group-item Java

```java
serialPort.openDiscoveryActivity();
```

:::

::::

::: tip 提示

**不想使用内置的搜索页面怎么办？**

可以设置自定义的搜索页面或者直接使用设备地址进行连接。详见[使用自定义的界面](/serialport/guide/discovery_connect/#使用自定义的界面)

:::

### 接收消息

使用方法 `setReceivedDataCallback(receivedDataCallback)`  设置一个接收消息监听器：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.setReceivedDataCallback { data ->

        }
```

:::

::: code-group-item Java

```java
serialPort.setReceivedDataCallback( (data) -> {
            
            return null;
        });
```

:::

::::

除此之外，你还可以在构建实例时配置监听器：

:::: code-group

::: code-group-item Kotlin

```kotlin
val serialPort = SerialPortBuilder
            .setReceivedDataCallback { data ->

            }
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .setReceivedDataCallback( (data) -> {

                    return null;
                })
                .build(this);
```

:::

::::

### 发送消息

使用方法 `sendData(data)` 发送消息：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.sendData("Hello World")
```

:::

::: code-group-item Java

```java
serialPort.sendData("Hello World");
```

:::

::::

**至此，你已经可以快速的开发一款能够完成基本收发数据的串口应用了。当然，`SerialPort` 还有着更多的功能，请继续阅读说明文档。**



 