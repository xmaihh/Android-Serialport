<!--[![](https://github.com/xmaihh/Android-Serialport/raw/master/art/logo.png)](https://code.google.com/archive/p/android-serialport-api/)-->
# Android-Serialport
Porting Google's official serial port library[android-serialport-api](https://code.google.com/archive/p/android-serialport-api/),only supports serial port name and baud rate. This item adds support check digit, data bit, stop bit, flow control configuration item.

<!--<img src="https://github.com/xmaihh/Android-Serialport/raw/master/art/compile_env.png" width="80%" height="80%" align="middle" alt="编译环境"/>-->


[![GitHub forks](https://img.shields.io/github/forks/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport/network)[![GitHub issues](https://img.shields.io/github/issues/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport/issues)[![GitHub stars](https://img.shields.io/github/stars/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport/stargazers)[![Source persent](https://img.shields.io/badge/Java-73.2%25-brightgreen.svg)](https://github.com/xmaihh/Android-Serialport/search?l=C)[![Jcenter2.0](https://img.shields.io/badge/jcenter-2.0-brightgreen.svg)](https://bintray.com/xmaihh/maven/serialport)[![Demo apk download](https://img.shields.io/crates/dv/rustc-serialize.svg)](https://fir.im/lcuy)
[![AppVeyor branch](https://img.shields.io/appveyor/ci/:user/:repo/:branch.svg)](https://github.com/xmaihh/Android-Serialport/tree/master)[![GitHub license](https://img.shields.io/github/license/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport)

# Document
- [中文](https://github.com/xmaihh/Android-Serialport/blob/master/README.md)

# Usage[![Download](https://api.bintray.com/packages/xmaihh/maven/serialport/images/download.svg)](https://bintray.com/xmaihh/maven/serialport/_latestVersion)
1. `Gradle`dependency
```
implementation 'tp.xmaihh:serialport:2.0'
```
2. `Maven`dependency
```
<dependency>
  <groupId>tp.xmaihh</groupId>
  <artifactId>serialport</artifactId>
  <version>2.0</version>
  <type>pom</type>
</dependency>
```
# Attribute
| Attributes | parameter|
| --- |  :---: |
|Baud rate | [BAUDRATE](https://github.com/xmaihh/Android-Serialport/blob/master/serialport/src/main/java/android_serialport_api/SerialPort.java) |
|Data bit |5,6,7,8 ; default value 8|
|Check Digit |No parity (NONE), odd parity (ODD), even parity (EVEN); default no parity|
| Stop bit | 1,2 ; default value 1 |
|Flow Control |No flow control (NONE), hardware flow control (RTS/CTS), software flow control (XON/XOFF); flow control is not used by default|
# Function
## 1.List the serial port
```
serialPortFinder.getAllDevicesPath();
```
## 2.Serial port property settings
```
serialHelper.setPort(String sPort);      //set the serial port
serialHelper.setBaudRate(int iBaud);     //set the baud rate
serialHelper.setStopBits(int stopBits);  //set the stop bit
serialHelper.setDataBits(int dataBits);  //set the data bit
serialHelper.setParity(int parity);      //set the check bit
serialHelper.setFlowCon(int flowcon);    //set the flow control
```
[![](https://img.shields.io/badge/warning-%09%20admonition-yellow.svg)](https://github.com/xmaihh/Android-Serialport)

Serial port property settings must be set before the function 'open()' is executed.
## 3. Open the serial port
```
serialHelper.open();
```
## 4.Close the serial port
```
serialHelper.close();
```
## 5.Send
```
serialHelper.send(byte[] bOutArray); // send byte[]
serialHelper.sendHex(String sHex);  // send Hex
serialHelper.sendTxt(String sTxt);  // send ASCII
```
## 6.Receiving
```
 @Override
protected void onDataReceived(final ComBean comBean) {
       Toast.makeText(getBaseContext(), new String(comBean.bRec, "UTF-8"), Toast.LENGTH_SHORT).show();
   }
```
# Demo APK
<img src="https://github.com/xmaihh/Android-Serialport/raw/master/art/screen.png" width="270" height="480" alt="演示效果"/>

[![apk Download](https://img.shields.io/crates/dv/rustc-serialize.svg)](https://fir.im/lcuy)

PC-side debugging tools  [Serial debugging tool for Win](https://github.com/xmaihh/Android-Serialport/raw/master/serial_port_utility_latest.exe)

# FAQ
* This library does not provide ROOT permissions, please open the serial port '666' permissions yourself.
```
adb shell  chmod 666 /dev/ttyS1
```
