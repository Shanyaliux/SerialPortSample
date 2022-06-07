# 服务端

现在，可以构建Android服务端，实现两台Android之间的蓝牙通信。(目前仅支持单设备连接)

## 构建实例

```kotlin
val serialPortServer = SerialPortServerBuilder
            .setServerName("SerialPortServer")
            .setServerUUID("00001101-0000-1000-8000-00805F9B34FB")
            .setServerReceivedDataCallback {
                
            }
            .setServerConnectStatusCallback { status, bluetoothDevice ->
                
            }
            .build(this)
```

- `setServerName` 设置服务端名称
- `setServerUUID` 设置服务端UUID，客户端连接时需设置传统设备的UUID与此相同
- `setServerReceivedDataCallback` 服务端接收消息监听
- `setServerConnectStatusCallback` 服务端连接状态监听
  - `status` 连接状态
  - `bluetoothDevice` 连接设备，当`status`为false则其为null

## 打开服务

只有打开服务后，客户端才可以连接到服务端。

```kotlin
serialPortServer.openServer()
```

## 关闭服务

```kotlin
serialPortServer.closeServer()
```

## 设置服务端可发现状态

默认打开服务则会自动设置为可发现，关闭服务则设置为不可见。

```kotlin
setServerDiscoverable(status)
```
- `status` 为 `Boolean`类型，表示可发现状态

## 断开连接

主动断开与客户端的连接

```kotlin
serialPortServer.disconnect()
```

## 发送消息

```kotlin
serialPortServer.sendData("Hello")
```


