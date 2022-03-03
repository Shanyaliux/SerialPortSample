<div align="center">
    <p>
    <h3>
      <b>
        SerialPort
      </b>
    </h3>
  </p>
  <p>
    <b>
      一个 Android 蓝牙串口通信的封装库
    </b>
  </p>
  <p>

 [![Website](https://img.shields.io/badge/Website-available-brightgreen?logo=e)](https://shanyaliux.cn/serialport/)
  </p>
  <p>
    <sub>
      Made with ❤︎ by
      <a href="https://github.com/shanyaliux">
        Shanya
      </a>
    </sub>
  </p>
  <br />
</div>

## SerialPort简介

SerialPort 是一个开源的对 Android 蓝牙串口通信的轻量封装库，轻松解决了构建自己的串口调试APP的复杂程度，让人可以专注追求自己设计，不用考虑蓝牙串口底层的配置。

- 链式调用，一次到位
- 传统与BLE兼顾
- 内置必备搜索界面
- 搜索、连接状态监听
- 自动重连机制
- Toast 提示内容修改

## 使用
编辑`Build.gradle`文件并添加以下依赖项。
```groovy
repositories {
  google()
  mavenCentral()
}

dependencies {
    implementation 'cn.shanyaliux.serialport:serialport:4.1.7'
}
```

## 说明文档

[https://shanyaliux.cn/serialport/](https://shanyaliux.cn/serialport/)

## Demo示例源码

[Java版本](https://gitee.com/Shanya/SerialPortDemoByJava)  
[Kotlin版本](https://gitee.com/Shanya/SerialPortDemoByKotlin)
