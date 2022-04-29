# Toast

## Configuration method

```kotlin
//whether to display
SerialPortToast.connectSucceeded.status = true
//prompt content (content is a string id)
SerialPortToast.connectSucceeded.content = R.string.connectSucceededToast
//Display duration Toast.LENGTH_SHORT or Toast.LENGTH_LONG
SerialPortToast.connectSucceeded.time = Toast.LENGTH_SHORT
```

## Optional configuration items

|          item          |                          describe                           |                           defaults                           |
| :--------------------: | :---------------------------------------------------------: | :----------------------------------------------------------: |
|    connectSucceeded    |              When the connection is successful              |                     Connection succeeded                     |
|     connectFailed      |                  When the connection fails                  |                      Connection failed                       |
|       disconnect       |                      When disconnected                      |                          Disconnect                          |
|      connectFirst      |            Send data when no device is connected            |               Please connect the device first                |
|    disconnectFirst     | Perform the connect operation after the device is connected |                   Please disconnect first                    |
|       permission       |          Ask whether to enable location permission          |           Please enable location permission first            |
|         hexTip         |   When sending hexadecimal, the data format is incorrect    | Please keep two digits for each hexadecimal data entered, and the insufficient is 0 in the front |
| openBluetoothSucceeded |        When the bluetooth is turned on successfully         |               Bluetooth turned on successfully               |
|  openBluetoothFailed   |               When turning on bluetooth fails               |                 Failed to turn on bluetooth                  |