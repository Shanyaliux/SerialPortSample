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
      一个安卓蓝牙工具库
    </b>
  </p>
  <p>

[![docs](https://img.shields.io/badge/docs-latest-blue)](https://serialportsample.readthedocs.io/zh_CN/latest/)
[![license](https://img.shields.io/github/license/open-mmlab/mmdetection.svg)](https://github.com/Shanyaliux/SerialPortSample/blob/master/LICENSE)
[![open issues](https://isitmaintained.com/badge/open/open-mmlab/mmdetection.svg)](https://github.com/Shanyaliux/SerialPortSample/issues)
  <br />
</div>

## 简介
[English](README.md) | 简体中文

SerialPort 是一个开源的对 Android 蓝牙串口通信的轻量封装库，轻松解决了构建自己的串口调试APP的复杂程度，让人可以专注追求自己设计，不用考虑蓝牙串口底层的配置。

- 链式调用，一次到位
- 传统与BLE兼顾
- 内置必备搜索界面
- 搜索、连接状态监听
- 自动重连机制
- Toast 提示内容修改

## 开源许可证
该项目采用 [Apache 2.0 开源许可证](LICENSE)。

## 更新日志
最新的 **4.2.0** 版本已经在 7/6/2022 发布:  
- 修复一些关于蓝牙权限的编译告警
- 升级`Kotlin`和`Gradle`版本
- 标记`ConnectionResultCallback`过时
- 新增Ble设备发送字节数组
- 新增Ble设备可以工作回调
- 新增服务端配置

更多更新日志见: [更新日志](docs/zh_CN/changelog.md)

## 安装
请参考 [安装文档](docs/zh_CN/tutorials/install.md) 进行安装。

## 快速入门
请参考 [快速入门文档_Kotlin版本](docs/zh_CN/tutorials/getting_started_kotlin.md) 或 [快速入门文档_Java版本](docs/zh_CN/tutorials/getting_started_java.md) 学习 SerialPort 的基本使用。

我们提供完整的文档。 [文档地址](https://serialport.readthedocs.io/zh_CN/latest/)

## 最简Demo源码
以下是基于SerialPort实现蓝牙App的Java和Kotlin最简Demo源码  
[Java](javademo)  
[Kotlin](kotlindemo)

## 欢迎加入 SerialPort 交流群
<div align="center">
<img src="docs/en/_static/image/qq.png" height="400" />
</div>
