# Received and send

## Set data format

Use the methods `setReadDataType(type)` and `setSendDataType(type)` to format the technique data:

### Set the received message format

```kotlin
//SerialPort.READ_HEX hex
//SerialPort.READ_STRING string
//If not set, the default string form
serialPort.setReadDataType(SerialPort.READ_HEX)
```

In addition to this, you can also set the received data format when building the instance:

```kotlin
//SerialPort.READ_HEX hex
//SerialPort.READ_STRING string
//If not set, the default string form
val serialPort = SerialPortBuilder
            .setReadDataType(SerialPort.READ_HEX)
            .build(this)
```

### Set the send data format

```kotlin
//SerialPort.SEND_HEX hex
//SerialPort.SEND_STRING string
//If not set, the default string form
serialPort.setSendDataType(SerialPort.SEND_HEX )
```

In addition to this, you can also set the send data format when building the instance:

```kotlin
//SerialPort.SEND_HEX hex
//SerialPort.SEND_STRING string
//If not set, the default string form
val serialPort = SerialPortBuilder
            .setSendDataType(SerialPort.SEND_HEX)
            .build(this)
```

Currently, the data sending and receiving for BLE devices does not support the setting format, only the string format is supported. If you really need the hexadecimal data format, you can temporarily implement it by referring to the processing method of traditional equipment.

Reference code link: [HexStringToString](https://gitee.com/Shanya/SerialPortSample/blob/master/serialport/src/main/java/world/shanya/serialport/tools/SerialPortToolsByKotlin.kt#L112)、[StringToHex](https://gitee.com/Shanya/SerialPortSample/blob/master/serialport/src/main/java/world/shanya/serialport/tools/SerialPortToolsByKotlin.kt#L199)

## Receive message

### string and hex

Use the method `setReceivedDataCallback(receivedDataCallback)` to set up a received message listener:

```kotlin
serialPort.setReceivedDataCallback { data ->

        }
```

In addition to this, you can also configure the listener when building the instance:

```kotlin
val serialPort = SerialPortBuilder
            .setReceivedDataCallback { data ->

            }
            .build(this)
```

### Byte array

When receiving a message, you can also choose to obtain a **byte array** as follows:

```kotlin
serialPort.setReceivedBytesCallback { bytes ->

        }
```

In addition to this, you can also configure the listener when building the instance:

```kotlin
val serialPort = SerialPortBuilder
            .setReceivedBytesCallback { bytes ->

            }
            .build(this)
```

## Send a message

Send a message using the method `sendData(data)`:

### String

```kotlin
serialPort.sendData("Hello World")
```

### hex

```kotlin
serialPort.sendData("0C FF")
```

All hexadecimals should be **two digits**, with 0 in front of the less than two digits, case-insensitive.

### BLE device send bytes

Now, BLE device support send bytes

```java
serialPort.sendData(bytes)
```