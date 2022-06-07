# 安装

编辑`Build.gradle`文件并添加以下依赖项。

```groovy
dependencies {
    implementation 'cn.shanyaliux.serialport:serialport:4.2.0'
}
```

如果你需要使用`4.1.6`及其以下版本，则按如下操作：  
1. 添加 `JitPack` 仓库
   将 `JitPack` 存储库添加到您的构建文件中
   ```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
   ```
2. 添加依赖
    ```groovy
    dependencies {
        implementation 'com.gitee.Shanya:SerialPortSample:4.1.6'        //国内仓库
        implementation 'com.github.Shanyaliux:SerialPortSample:4.1.6'   //国外仓库
    }
    ```