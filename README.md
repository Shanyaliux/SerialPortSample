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
      An android bluetooth kit library
    </b>
  </p>
  <p>

[![docs](https://img.shields.io/badge/docs-latest-blue)](https://serialportsample.readthedocs.io/en/latest/)
[![license](https://img.shields.io/github/license/open-mmlab/mmdetection.svg)](https://github.com/Shanyaliux/SerialPortSample/blob/master/LICENSE)
[![open issues](https://isitmaintained.com/badge/open/open-mmlab/mmdetection.svg)](https://github.com/Shanyaliux/SerialPortSample/issues)
  <br />
</div>

## Introduction
English | [简体中文](README_zh-CN.md)

SerialPort is an open source android bluetooth lightweight package library that easily solves the complexity of building your own bluetooth application, allowing people to focus on their own designs without considering the underlying configuration of android bluetooth.

- Chain call
- Legacy and BLE
- Built-in must-have search interface
- Search and connection status monitoring
- Automatic reconnection mechanism
- Toast prompt content modification

## License
This project is released under the [Apache 2.0 license](LICENSE).

## Changelog
**4.2.0** was released in 7/6/2022:
- [Fix] Some compilation warnings about bluetooth permissions
- [Modify] Upgrade `Kotlin` and `Gradle` versions
- [Modify] Mark `ConnectionResultCallback` obsolete
- [Feature] Added Ble device to send byte array
- [Feature] Added Ble device can work callback
- [Feature] Added Server

For more changelogs see: [Changelog](docs/en/changelog.md)

## Installation
Please refer to [install.md](docs/en/tutorials/install.md) for installation.

## Getting Started
Please see [Basic usage Kotlin.md](docs/en/tutorials/getting_started_kotlin.md) or [Basic usage Java.md](docs/en/tutorials/getting_started_java.md) for the basic usage of SerialPort.

We provide complete documentation. [documentation](https://serialport.readthedocs.io/en/latest/)

## Demo sample source code
The following is the simplest Demo source code of Java and Kotlin based on SerialPort to implement Bluetooth App  
[Java](javademo)  
[Kotlin](kotlindemo)
