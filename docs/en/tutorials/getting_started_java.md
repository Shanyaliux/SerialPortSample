# Basic usage (Java)

### Build SerialPort instance

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE.build(this);
```

### Search device

Use the method `doDiscovery(context)` to search for devices:

```java
serialPort.doDiscovery(this);
```

Use the methods `getPairedDevicesListBD()` and `getUnPairedDevicesListBD()` to get search results:

```java
serialPort.getPairedDevicesListBD();	//Get a list of paired devices
serialPort.getUnPairedDevicesListBD();	//Get a list of unpaired devices
```

**If the search is not over, the list of unpaired devices may be empty or incomplete.**


### Connect the device

Setting the correct UUID is an essential step in order to successfully connect the device and complete the communication.

#### Set legacy device UUID

Use the static method `setLegacyUUID(uuid)` of `SerialPort` to set the UUID of the legacy device:

```java
SerialPort.Companion.setLegacyUUID("00001101-0000-1000-8000-00805F9B34FB");
```

For traditional devices **generally**, you can use the default UUID without setting UUID.


#### Set BLE device UUID

Use the static method `setLegacyUUID(uuid)` of `SerialPort` to set the UUID of the BLE device:

```java
SerialPort.Companion.setBleUUID("0000ffe1-0000-1000-8000-00805f9b34fb");
```

In most cases, BLE devices need to set UUID. For specific UUID, you can check the manual or consult the seller.

In addition, you can also use the method `printPossibleBleUUID()` to print out a feasible UUID, and try it yourself:

```java
serialPort.printPossibleBleUUID()
```


#### Establish connection

Use the method `openDiscoveryActivity()` to open the built-in search page and select a device to connect to:

```java
serialPort.openDiscoveryActivity();
```

**What if you don't want to use the built-in search page? **

You can set up a custom search page or connect directly using the device address. See [Use a custom interface](/discovery_connect_java.html#id3)


### Receive message

Use the method `setReceivedDataCallback(receivedDataCallback)` to set up a received message listener:


```java
serialPort.setReceivedDataCallback( (data) -> {
            
            return null;
        });
```

In addition to this, you can also configure the listener when building the instance:

```java
SerialPort serialPort = SerialPortBuilder.INSTANCE
                .setReceivedDataCallback( (data) -> {

                    return null;
                })
                .build(this);
```

### Send a message

Send a message using the method `sendData(data)`:

```java
serialPort.sendData("Hello World");
```

**At this point, you can quickly develop a serial port application that can complete basic sending and receiving data. Of course, `SerialPort` has many more functions, please continue to read the documentation. **

