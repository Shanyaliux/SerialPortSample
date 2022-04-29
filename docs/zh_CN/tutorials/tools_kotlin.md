# 工具

## 打印UUID及其属性

若我们不能知晓当前BLE设备的UUID可以调用函数`printPossibleBleUUID`来打印出当前连接设备的可选UUID

其中`Properties` 为二进制数，其每一位对应的意思见下表：

| 数值（以十六进制表示） |              意思               |
| :--------------------: | :-----------------------------: |
|          0x01          |          broadcastable          |
|          0x02          |            readable             |
|          0x04          | can be written without response |
|          0x08          |         can be written          |
|          0x10          |      supports notification      |
|          0x20          |       supports indication       |
|          0x40          |  supports write with signature  |
|          0x80          |     has extended properties     |



## 字符串转换成十六进制

```kotlin
/**
 * 字符串转换成十六进制
 * @param str 待转换的字符串
 * @return 十六进制数组
 */
DataUtil.string2hex("Hello")
```

## 字节数组按要求的编码格式转换成字符串

```kotlin
/**
 * 字节数组按要求的编码格式转换成字符串
 * @param bytes 带转换的字节数组
 * @param charsetName 要求的编码格式
 * @return 转换成功的字符串
 */
SerialPortTools.bytes2string(bytes, "GBK")
```

## 字符串按要求的编码格式转换成字节数组

```kotlin
/**
 * 字符串按要求的编码格式转换成字节数组
 * @param string 带转换的字符串
 * @param charsetName 要求的编码格式
 * @return 转换成功的字节数组
 */
SerialPortTools.bytes2string("Hello", "GBK")
```
