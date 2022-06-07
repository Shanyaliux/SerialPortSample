# Install

Edit the `Build.gradle` file and add the following dependencies.

```groovy
dependencies {
    implementation 'cn.shanyaliux.serialport:serialport:4.2.0'
}
```

If you need to use `4.1.6` and below, do as follows:  
1. Add `JitPack` repository
   Add the `JitPack` repository to your build file
   ```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
   ```
2. add dependencies
    ```groovy
    dependencies {
        implementation 'com.gitee.Shanya:SerialPortSample:4.1.6'        //Chinese warehouse
        implementation 'com.github.Shanyaliux:SerialPortSample:4.1.6'   //Foreign warehouse
    }
    ```