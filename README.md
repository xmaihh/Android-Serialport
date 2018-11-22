[![](https://github.com/xmaihh/Android-Serialport/raw/master/art/logo.png)](https://code.google.com/archive/p/android-serialport-api/)
# Android-Serialport
移植谷歌官方串口库[android-serialport-api](https://code.google.com/archive/p/android-serialport-api/),仅支持串口名称及波特率，该项目添加支持配置校验位、数据位、停止位、流控配置项

<img src="https://github.com/xmaihh/Android-Serialport/raw/master/art/compile_env.png" width="80%" height="80%" align="middle" alt="编译环境"/>


[![GitHub forks](https://img.shields.io/github/forks/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport/network)[![GitHub issues](https://img.shields.io/github/issues/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport/issues)[![GitHub stars](https://img.shields.io/github/stars/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport/stargazers)[![Source persent](https://img.shields.io/badge/Java-73.2%25-brightgreen.svg)](https://github.com/xmaihh/Android-Serialport/search?l=C)[![Jcenter](https://img.shields.io/badge/jcenter-1.0-brightgreen.svg)](https://bintray.com/xmaihh/maven/serialport)[![Demo apk download](https://img.shields.io/crates/dv/rustc-serialize.svg)](https://fir.im/lcuy)
[![AppVeyor branch](https://img.shields.io/appveyor/ci/:user/:repo/:branch.svg)](https://github.com/xmaihh/Android-Serialport/tree/master)[![GitHub license](https://img.shields.io/github/license/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport)


# 使用依赖
- `Maven`引用
```
<dependency>
  <groupId>tp.xmaihh</groupId>
  <artifactId>serialport</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```
- `Gradle`引用
```
implementation 'tp.xmaihh:serialport:1.0'
```
# 属性支持
| 属性 | 参数|
| --- |  :---: |
|波特率 | [BAUDRATE](https://github.com/xmaihh/Android-Serialport/blob/master/serialport/src/main/java/android_serialport_api/SerialPort.java) |
|数据位 |5,6,7,8 ;默认值8|
|校验位 |无奇偶校验(NONE), 奇校验(ODD), 偶校验(EVEN); 默认无奇偶校验|
| 停止位| 1,2 ;默认值1 |
|流控 |不使用流控(NONE), 硬件流控(RTS/CTS), 软件流控(XON/XOFF); 默认不使用流控|
# 代码功能
## 1.列出串口列表
```
serialPortFinder.getAllDevicesPath();
```
## 2.串口属性设置
```
serialHelper.setPort(String sPort);      //设置串口
serialHelper.setBaudRate(int iBaud);     //设置波特率
serialHelper.setStopBits(int stopBits);  //设置停止位
serialHelper.setDataBits(int dataBits);  //设置数据位
serialHelper.setParity(int parity);      //设置校验位
serialHelper.setFlowCon(int flowcon);    //设置流控
```
## 3.打开串口
[![](https://img.shields.io/badge/warning-%09%20admonition-yellow.svg)](https://github.com/xmaihh/Android-Serialport)
属性设置需在执行`open()`函数之前才能设置生效
```
serialHelper.open();
```
## 4.关闭串口
```
serialHelper.close();
```
## 5.发送
```
serialHelper.send(byte[] bOutArray); // 发送byte[]
serialHelper.sendHex(String sHex);  // 发送Hex
serialHelper.sendTxt(String sTxt);  // 发送ASCII
```
## 6.接收
```
 @Override
protected void onDataReceived(final ComBean comBean) {
       Toast.makeText(getBaseContext(), new String(comBean.bRec, "UTF-8"), Toast.LENGTH_SHORT).show();
   }
```
# 完整Demo地址
[![apk下载](https://img.shields.io/crates/dv/rustc-serialize.svg)](https://fir.im/lcuy)
<img src="https://github.com/xmaihh/Android-Serialport/raw/master/art/screen.png" width="270" height="480" alt="演示效果"/>

PC端调试工具 [友善串口调试工具](https://github.com/xmaihh/SerialportDemo/blob/master/serial_port_utility_latest.exe)
