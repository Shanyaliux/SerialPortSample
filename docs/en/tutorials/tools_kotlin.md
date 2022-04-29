# Tools

## Print UUID and its attributes

If we don't know the UUID of the current BLE device, we can call the function `printPossibleBleUUID` to print out the optional UUID of the currently connected device

Where `Properties` is a binary number, and the meaning of each bit is shown in the following table:

| value (in hexadecimal) |              mean               |
| :--------------------: | :-----------------------------: |
|          0x01          |          broadcastable          |
|          0x02          |            readable             |
|          0x04          | can be written without response |
|          0x08          |         can be written          |
|          0x10          |      supports notification      |
|          0x20          |       supports indication       |
|          0x40          |  supports write with signature  |
|          0x80          |     has extended properties     |



## String2hex

```kotlin
/**
 * Convert string to hexadecimal
 * @param str String to convert
 * @return hex array
 */
DataUtil.string2hex("Hello")
```

## Bytes2string

```kotlin
/**
 * The byte array is converted into a string according to the required encoding format
 * @param bytes byte array to convert
 * @param charsetName Required encoding format
 * @return Converted string
 */
SerialPortTools.bytes2string(bytes, "GBK")
```

## String2bytes

```kotlin
/**
 * The string is converted into a byte array according to the required encoding format
 * @param string String to convert
 * @param charsetName Required encoding format
 * @return Converted byte array
 */
SerialPortTools.bytes2string("Hello", "GBK")
```
