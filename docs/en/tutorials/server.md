# Server

Now, an Android server can be built to implement Bluetooth communication between two Androids. (Currently only supports single device connection)

## Build instance

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

- `setServerName` Set server name
- `setServerUUID` Set the UUID of the server, the UUID of the traditional device needs to be set to the same as this when the client connects
- `setServerReceivedDataCallback` The server receives message monitoring
- `setServerConnectStatusCallback` Server connection status monitoring
  - `status` Connection Status
  - `bluetoothDevice` Connected device, null when `status` is false

## Open server

Only after the service is opened, the client can connect to the server.

```kotlin
serialPortServer.openServer()
```

## Close server

```kotlin
serialPortServer.closeServer()
```

## Set the server discoverable state

By default, if the server is turned on, it will be automatically set to be discoverable, and if the server is turned off, it will be set to be invisible.

```kotlin
setServerDiscoverable(status)
```
- `status` is of type `Boolean`, indicating the discoverable status

## Disconnect

Actively disconnect from the client

```kotlin
serialPortServer.disconnect()
```

## Send

```kotlin
serialPortServer.sendData("Hello")
```


