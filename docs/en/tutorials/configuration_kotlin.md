# Configuration

## Debug mode

When debugging the program, we can turn on the debugging mode, which will print a variety of log information, and turn off this switch when the APP is officially released to reduce resource overhead. The settings are as follows:

```kotlin
val serialPort = SerialPortBuilder
            .isDebug(true)
            .build(this)
```

## Auto reconnect

### Reconnect at startup

After this function is enabled, an automatic reconnection will be performed when the instance is constructed, and the reconnection object is the last **successfully connected** device. The settings are as follows:

```kotlin
val serialPort = SerialPortBuilder
            .autoConnect(true)
            .build(this)
```

### Automatic reconnection at intervals

After this function is turned on, it will automatically reconnect once at intervals (the time can be set by yourself), and the reconnection object is the last **successfully connected** device. The settings are as follows:

```kotlin
val serialPort = SerialPortBuilder
			//The second parameter is the interval time, 
            //if not specified, the default is 10000Ms
            .setAutoReconnectAtIntervals(true, 10000)
            .build(this)
```

## Ignore unnamed devices

When this feature is turned on, devices with empty device names are automatically ignored when searching for devices. The settings are as follows:

```kotlin
val serialPort = SerialPortBuilder
            .isIgnoreNoNameDevice(true)
            .build(this)
```

**For some Bluetooth devices, the device name may be empty when connecting for the first time. Please enable this function according to the situation.**

## Automatically open the search interface

After enabling this function, when sending data, if it finds that the device is not connected, it will automatically open the built-in search page. The settings are as follows:

```kotlin
val serialPort = SerialPortBuilder
            .autoOpenDiscoveryActivity(true)
            .build(this)
```

## Automatic conversion of hexadecimal data

When this function is turned on, when the received data is in hexadecimal, it will be automatically converted into a string. The settings are as follows:

```kotlin
val serialPort = SerialPortBuilder
            .autoHexStringToString(true)
            .build(this)
```

Of course, you can also do the conversion manually using the method `hexStringToString(hexString)`:

```kotlin
string = serialPort.hexStringToString(hexString)
```

## Built-in search page to choose connection method
After enabling this function, when you click the device to connect on the built-in page, you can manually select the connection method. But please note that if your device does not support the connection method you selected, the connection will not be successful.

```kotlin
val serialPort = SerialPortBuilder
            .setOpenConnectionTypeDialogFlag(true)
            .build(this)
```

## Configurator

Configurator You can pass the above multiple configurations into `SerialPortBuilder` at one time.

```kotlin
val config = SerialPortConfig()
val serialPort = SerialPortBuilder
            .setConfig(config)
            .build(this)
```

The parameters that can be set by the configurator are shown in the following table (bold indicates the default value):

|           parameter name           |                        value                         |
| :--------------------------: | :-----------------------------------------------: |
|            debug             |              (bool) true / **false**              |
|         UUID_LEGACY          | (string) **00001101-0000-1000-8000-00805F9B34FB** |
|           UUID_BLE           | (string) **00001101-0000-1000-8000-00805F9B34FB** |
|        UUID_BLE_READ         |                  (string) **none**                  |
|        UUID_BLE_SEND         |                  (string) **none**                  |
|         autoConnect          |              (bool) true / **false**              |
|        autoReconnect         |              (bool) true / **false**              |
|     reconnectAtIntervals     |                  (int) **10000**                  |
|  autoOpenDiscoveryActivity   |              (bool) true / **false**              |
|    autoHexStringToString     |              (bool) true / **false**              |
|         readDataType         | **SerialPort.READ_STRING** / SerialPort.READ_HEX  |
|         sendDataType         | **SerialPort.SEND_STRING** / SerialPort.SEND_HEX  |
|      ignoreNoNameDevice      |              (bool) true / **false**              |
| openConnectionTypeDialogFlag |              (bool) true / **false**              |

**Among them, please refer to the precautions for setting UUID: [ble device set UUID](./discovery_connect_kotlin.html#ble)**

## Set search duration

Use this method to configure how long to search for devices:

```kotlin
//The parameter is time, in milliseconds
val serialPort = SerialPortBuilder
            .setDiscoveryTime(10000)
            .build(this)
```

