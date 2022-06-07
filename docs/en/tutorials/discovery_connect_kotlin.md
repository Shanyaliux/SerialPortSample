# Discovery and connect

## Built-in interface

In order to help develop serial communication applications more conveniently and quickly, we have integrated a necessary search and connection page internally. Use the method `openDiscoveryActivity()` to open a built-in interface:

```kotlin
serialPort.openDiscoveryActivity()
```

## Use a custom interface

Of course, in more cases our search and connection pages need to be more beautiful and customizable. Then, you can use the method `serialPort.openDiscoveryActivity(intent)` to open a custom page:

```kotlin
//Here you can modify it to your custom Activity
val intent = Intent(this,DiscoveryActivity::class.java)
serialPort.openDiscoveryActivity(intent)
```

## Search device

### Start search

Use the method `doDiscovery(context)` to start searching for devices:

```kotlin
serialPort.doDiscovery(this)
```

### Stop searching

Use the method `cancelDiscovery(context)` to start searching for devices:

```kotlin
serialPort.cancelDiscovery(this)
```

### Monitor search status

Use the method `setDiscoveryStatusWithTypeCallback(discoveryStatusWithTypeCallback)` or `setDiscoveryStatusCallback(discoveryStatusCallback)` to set a search status listener:

```kotlin
//status is search status
serialPort.setDiscoveryStatusCallback{ status ->  
   
}
//Search for listeners with status band type
//deviceType = SerialPort.DISCOVERY_BLE Search for BLE devices
//deviceType = SerialPort.DISCOVERY_LEGACY Search traditional types
//status is search status
serialPort.setDiscoveryStatusWithTypeCallback { deviceType, status ->
            
}
```

In addition to this, you can also configure the listener when building the instance:

```kotlin
//status is search status
val serialPort = SerialPortBuilder
            .setDiscoveryStatusCallback { status ->

            }
            .build(this)
//Search for listeners with status band type
//deviceType = SerialPort.DISCOVERY_BLE Search for BLE devices
//deviceType = SerialPort.DISCOVERY_LEGACY Search traditional types
//status is search status
val serialPort = SerialPortBuilder
            .setDiscoveryStatusWithTypeCallback { deviceType, status -> 
                
            }
            .build(this)
```

### Get search results

Use the methods `getPairedDevicesListBD()` and `getUnPairedDevicesListBD()` to get search results:

```kotlin
serialPort.getPairedDevicesListBD()		//Get a list of paired devices
serialPort.getUnPairedDevicesListBD()	//Get a list of unpaired devices
```

If the search does not end, the acquired list of unpaired devices may be empty or incomplete.

## Connect the device

Setting the correct UUID is an essential step in order to successfully connect the device and complete the communication.

### Traditional equipment

#### Set UUID

Use the static method `setLegacyUUID(uuid)` of `SerialPort` to set the UUID of the legacy device:

```kotlin
SerialPort.setLegacyUUID("00001101-0000-1000-8000-00805F9B34FB")
```

For traditional devices **generally**, you can use the default UUID without setting UUID.

#### Establish connection

Use the method `connectLegacyDevice(address)` to establish a connection with a legacy device:

```kotlin
serialPort.connectLegacyDevice("98:D3:32:21:67:D0")
```

### BLE device

#### Set UUID

Use the static method `setBleUUID(uuid)` of `SerialPort` to set the UUID of the BLE device, or use `setBleSendUUID` and `setBleReadUUID` to set the send and receive UUID independently:

```kotlin
SerialPort.setBleUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
SerialPort.setBleReadUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
SerialPort.setBleSendUUID("0000ffe1-0000-1000-8000-00805f9b34fb")
```

If the UUID is set independently, the one set independently shall prevail.

In most cases, BLE devices need to set UUID. For specific UUID, you can check the manual or consult the seller.

In addition, you can also use the method `printPossibleBleUUID()` to print out the feasible UUID, see for details: [print uuid and its attributes](./tools_kotlin.html#uuid)

#### Establish connection

Use the method `connectBle(address)` to establish a connection to a legacy device:

```kotlin
serialPort.connectBle("98:D3:32:21:67:D0")
```

### Disconnect

Use the method `disconnect()` to establish a connection to a legacy device:

```kotlin
serialPort.disconnect()
```

### Monitor connection status

Use the method `setConnectionStatusCallback(connectionStatusCallback)` to set a connection status listener:

```kotlin
serialPort.setConnectStatusCallback { status, bluetoothDevice ->  
   
}
```

In addition to this, you can also configure the listener when building the instance:

```kotlin
val serialPort = SerialPortBuilder
            .setConnectionStatusCallback { status, bluetoothDevice -> 
                
            }
            .build(this)
```

The `bluetoothDevice` used here is the official class, which contains various information about the Bluetooth device. see details [official documentation](https://developer.android.google.cn/reference/kotlin/android/bluetooth/BluetoothDevice)

In previous versions, a custom `Device` class was used (deprecated), which contains: device name, device address, and device type. It is implemented as follows:

```kotlin
@Deprecated("This class is deprecated in version 4.0.0 and will directly use the official BluetoothDevice class instead")
data class Device(
    val name:String,
    val address:String,
    val type:Int = 0
)
```

### BLE can work callback

This callback is triggered after the BLE device is successfully connected and can work, and can be used to configure the automatic sending of messages after the connection is successful. The method of use is as follows:

```kotlin
serialPort.setBleCanWorkCallback {

}
```

In addition to this, you can also configure the listener when building the instance:

```kotlin
val serialPort = SerialPortBuilder
            .setBleCanWorkCallback {
   
			}
            .build(this)
```

