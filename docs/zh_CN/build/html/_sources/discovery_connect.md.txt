# 搜索和连接

## 内置的界面

为了更加方便快速的帮助开发串口通信应用程序，我们内部集成了一个必备的搜索和连接页面，使用方法 `openDiscoveryActivity()` 打开一个内置的界面：

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

## 使用自定义的界面

当然了，在更多的情况我们的搜索和连接页面需要更加的美观和定制化。那么，可以使用方法 `serialPort.openDiscoveryActivity(intent)` 打开一个你自定义的页面：

:::: code-group

::: code-group-item Kotlin

```kotlin
//这里修改为你自定义的Activity即可
val intent = Intent(this,DiscoveryActivity::class.java)
serialPort.openDiscoveryActivity(intent)
```

:::

::: code-group-item Java

```java
//这里修改为你自定义的Activity即可
Intent intent = new Intent(this, DiscoveryActivity.class);
serialPort.openDiscoveryActivity(intent);
```

:::

::::

## 搜索设备

### 开始搜索

使用方法 `doDiscovery(context)` 即可开始搜索设备：

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

### 停止搜索

使用方法 `cancelDiscovery(context)` 即可开始搜索设备：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.cancelDiscovery(this)
```

:::

::: code-group-item Java

```java
serialPort.cancelDiscovery(this);
```

:::

::::

### 搜索状态的监听

使用方法 `setDiscoveryStatusWithTypeCallback(discoveryStatusWithTypeCallback)` 或者 `setDiscoveryStatusCallback(discoveryStatusCallback)`  设置一个搜索状态监听器：

:::: code-group

::: code-group-item Kotlin

```kotlin
//status 为搜索状态
serialPort.setDiscoveryStatusCallback{ status ->  
   
}
//搜索状态带类型的监听
//deviceType = SerialPort.DISCOVERY_BLE 搜索BLE设备
//deviceType = SerialPort.DISCOVERY_LEGACY 搜索传统类型
//status 为搜索状态
serialPort.setDiscoveryStatusWithTypeCallback { deviceType, status ->
            
}
```

:::

::: code-group-item Java

```java
//status 为搜索状态
serialPort.setDiscoveryStatusCallback((status) ->{  
   
   return null;
});
//搜索状态带类型的监听
//deviceType = SerialPort.DISCOVERY_BLE 搜索BLE设备
//deviceType = SerialPort.DISCOVERY_LEGACY 搜索传统类型
//status 为搜索状态
serialPort.setDiscoveryStatusWithTypeCallback((deviceType, status) -> {

return null;
});
```

:::

::::

除此之外，你还可以在构建实例时配置监听器：

:::: code-group

::: code-group-item Kotlin

```kotlin
//status 为搜索状态
val serialPort = SerialPortBuilder
            .setDiscoveryStatusCallback { status ->

            }
            .build(this)
//搜索状态带类型的监听
//deviceType = SerialPort.DISCOVERY_BLE 搜索BLE设备
//deviceType = SerialPort.DISCOVERY_LEGACY 搜索传统类型
//status 为搜索状态
val serialPort = SerialPortBuilder
            .setDiscoveryStatusWithTypeCallback { deviceType, status -> 
                
            }
            .build(this)
```

:::

::: code-group-item Java

```java
//status 为搜索状态
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .setDiscoveryStatusCallback( (status) -> {

                    return null;
                })
                .build(this);
//搜索状态带类型的监听
//deviceType = SerialPort.DISCOVERY_BLE 搜索BLE设备
//deviceType = SerialPort.DISCOVERY_LEGACY 搜索传统类型
//status 为搜索状态
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .setDiscoveryStatusWithTypeCallback( (deviceType, status) -> {
                    
                    return null;
                })
                .build(this);
```

:::

::::

### 获取搜索结果

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

## 连接设备

想要成功的连接设备，并且完成通信，设置正确的UUID是必不可少的一步。

### 传统设备

#### 设置UUID

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

#### 建立连接

使用方法 `connectLegacyDevice(address)` 与传统设备建立连接：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.connectLegacyDevice("98:D3:32:21:67:D0")
```

:::

::: code-group-item Java

```java
serialPort.connectLegacyDevice("98:D3:32:21:67:D0");
```

:::

::::

### BLE设备

#### 设置UUID

使用 `SerialPort` 的静态方法 `setBleUUID(uuid)` 设置BLE设备的UUID， 或者使用`setBleSendUUID` 和 `setBleReadUUID` 分别独立设置发送和接收的UUID：

:::: code-group

::: code-group-item Kotlin

```kotlin
SerialPort.setBleUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
SerialPort.setBleReadUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
SerialPort.setBleSendUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
```

:::

::: code-group-item Java

```java
SerialPort.Companion.setBleUUID("0000ffe1-0000-1000-8000-00805f9b34fb");
SerialPort.Companion.setBleReadUUID("0000ffe1-0000-1000-8000-00805f9b34fb");
SerialPort.Companion.setBleSendUUID("0000ffe1-0000-1000-8000-00805f9b34fb");
```

:::

::::

::: warning 注意

如果独立设置了UUID，则以独立设置的为准。  

BLE设备大多数情况下都需要设置UUID，具体的UUID可以查询手册或咨询卖家。

除此之外，也可以使用方法 `printPossibleBleUUID()` 打印出可行的UUID，详情见：[打印uuid及其属性](/serialport/guide/tools.html#打印uuid及其属性)

:::

#### 建立连接

使用方法 `connectBle(address)` 与传统设备建立连接：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.connectBle("98:D3:32:21:67:D0")
```

:::

::: code-group-item Java

```java
serialPort.connectBle("98:D3:32:21:67:D0");
```

:::

::::

### 断开连接

使用方法 `disconnect()` 与传统设备建立连接：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.disconnect()
```

:::

::: code-group-item Java

```java
serialPort.disconnect();
```

:::

::::

### 连接状态的监听

使用方法 `setConnectionStatusCallback(connectionStatusCallback)` 设置一个连接状态的监听器：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.setConnectStatusCallback { status, bluetoothDevice ->  
   
}
```

:::

::: code-group-item Java

```java
serialPort.setConnectionStatusCallback((status,bluetoothDevice)->{
            
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
            .setConnectionStatusCallback { status, bluetoothDevice -> 
                
            }
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .setConnectionStatusCallback( (status, bluetoothDevice) -> {

                    return null;
                })
                .build(this);
```

:::

::::

::: warning 注意

这里的 `bluetoothDevice` 使用的时官方的类，其包含了蓝牙的设备的各种信息。详见[官方文档](https://developer.android.google.cn/reference/kotlin/android/bluetooth/BluetoothDevice)

在之前版本使用的是自定义的 `Device` 类（不建议使用），其包含了：设备名、设备地址、设备类型。其实现如下：

```kotlin
@Deprecated("该类在4.0.0版本被弃用,将直接使用官方的BluetoothDevice类代替")
data class Device(
    val name:String,
    val address:String,
    val type:Int = 0
)
```

:::

### 连接结果的监听

使用方法 `setConnectionResultCallback(connectionResultCallback)` 设置一个连接结果的监听器：

:::: code-group

::: code-group-item Kotlin

```kotlin
serialPort.setConnectionResultCallback { result, bluetoothDevice ->
   
}
```

:::

::: code-group-item Java

```java
serialPort.setConnectionResultCallback((result,bluetoothDevice)->{
            
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
            .setConnectionResultCallback { result, bluetoothDevice ->
   
			}
            .build(this)
```

:::

::: code-group-item Java

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .setConnectionResultCallback( (result, bluetoothDevice) -> {

                    return null;
                })
                .build(this);
```

:::

::::

::: tip 提示

若连接成功则 `result` 为 `true` ，`bluetoothDevice`为连接成功的设备

若连接失败则 `result` 为 `false` ，`bluetoothDevice`为 `null`

:::

