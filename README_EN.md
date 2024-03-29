<!--[![](https://github.com/xmaihh/Android-Serialport/raw/master/art/logo.png)](https://code.google.com/archive/p/android-serialport-api/)-->
# Android-Serialport
Porting Google's official serial port library [android-serialport-api](https://code.google.com/archive/p/android-serialport-api/),only supports serial port name and baud rate. This item adds support check digit, data bit, stop bit, flow control configuration item.

<!--<img src="https://github.com/xmaihh/Android-Serialport/raw/master/art/compile_env.png" width="80%" height="80%" align="middle" alt="编译环境"/>-->
<img src ="https://github.com/xmaihh/Android-Serialport/blob/master/art/logo.svg" height = 150 alt ="Android-Serialport"/>

[![GitHub forks](https://img.shields.io/github/forks/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport/network)[![GitHub issues](https://img.shields.io/github/issues/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport/issues)[![GitHub stars](https://img.shields.io/github/stars/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport/stargazers)[![Source persent](https://img.shields.io/badge/Java-73.2%25-brightgreen.svg)](https://github.com/xmaihh/Android-Serialport/search?l=C)[![Jcenter2.1](https://img.shields.io/badge/jcenter-2.1-brightgreen.svg)](https://bintray.com/xmaihh/maven/serialport)![Maven Central](https://img.shields.io/maven-central/v/io.github.xmaihh/serialport)
[![GitHub license](https://img.shields.io/github/license/xmaihh/Android-Serialport.svg)](https://github.com/xmaihh/Android-Serialport)

# Document

<p >
    <a href="https://github.com/xmaihh/Android-Serialport/blob/master/README.md">中文</a>
    | <a href="https://github.com/xmaihh/Android-Serialport/blob/master/README_EN.md">English</a>
</p>

# Usage
1. Open your root  `build.gradle` and add `mavenCentral()`: 
```
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```
2. To add a dependency to your project, specify a dependency configuration such as implementation in the dependencies block of your module's build.gradle file.
```
dependencies {
    implementation 'io.github.xmaihh:serialport:2.1.1'
}
```
# Attribute
| Attributes   |                                                              parameter                                                               |
|--------------|:------------------------------------------------------------------------------------------------------------------------------------:|
| Baud rate    | [BAUDRATE](https://github.com/xmaihh/Android-Serialport/blob/master/serialport/src/main/java/android_serialport_api/SerialPort.java) |
| Data bit     |                                                      5,6,7,8 ; default value 8                                                       |
| Parity bit   |              No parity (NONE), odd parity (ODD), even parity (EVEN), 0 parity(SPACE), 1 parity(MARK); default no parity              |
| Stop bit     |                                                        1,2 ; default value 1                                                         |
| Flow Control |    No flow control (NONE), hardware flow control (RTS/CTS), software flow control (XON/XOFF); flow control is not used by default    |
# Function
## 1.List the serial port
```
serialPortFinder.getAllDevicesPath();
```
## 2.Serial port property settings
```
serialHelper.setPort(String sPort);      //set serial port
serialHelper.setBaudRate(int iBaud);     //set baud rate
serialHelper.setStopBits(int stopBits);  //set stop bit
serialHelper.setDataBits(int dataBits);  //set data bit
serialHelper.setParity(int parity);      //set parity
serialHelper.setFlowCon(int flowcon);    //set flow control
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
## 7.Sticky processing
Support sticky package processing, the reason is seen in the [issues#1](https://github.com/xmaihh/Android-Serialport/issues/1) , the provided sticky package processing
- Not processed (default)
- First and last special character processing
- Fixed length processing
- Dynamic length processing

Supports custom sticky packet processing.

## Step 1
The first step is to implement the [AbsStickPackageHelper](https://github.com/xmaihh/Android-Serialport/blob/master/serialport/src/main/java/tp/xmaihh/serialport/stick/AbsStickPackageHelper.java) interface.
```
/**
 * Accept the message, the helper of the sticky packet processing, return the final data through inputstream, need to manually process the sticky packet, and the returned byte[] is the complete data we expected.
 * Note: This method will be called repeatedly until it resolves to a complete piece of data. This method is synchronous, try not to do time-consuming operations, otherwise it will block reading data.
 */
public interface AbsStickPackageHelper {
    byte[] execute(InputStream is);
}
```
## Step 2
Set sticky package processing
```
serialHelper.setStickPackageHelper(AbsStickPackageHelper mStickPackageHelper);
```

# Screenshots

![Screenshot showing screen](art/screen.png "Screenshot showing screen")

<a href="https://play.google.com/store/apps/details?id=com.ex.serialport"><img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="70"></a>

PC-side debugging tools  [Serial debugging tool for Win](https://github.com/xmaihh/Android-Serialport/raw/master/serial_port_utility_latest.exe)

# Changelog
## [2.1.2](https://github.com/xmaihh/Android-Serialport/tree/v2.1.2)
### Added
- Add support for setting parity: 0 parity(SPACE), 1 parity(MARK)
- Added support for setting custom baud rate [issues#26](https://github.com/xmaihh/Android-Serialport/issues/26)

## [2.1.1](https://github.com/xmaihh/Android-Serialport/tree/v2.1.1)
### Fixed
- Fix bug.[issues#17](https://github.com/xmaihh/Android-Serialport/issues/17)
- Migrate to MavenCentral

## [2.1](https://github.com/xmaihh/Android-Serialport/tree/v2.1)
### Added
- Add support settings to receive data sticky packet processing, support for setting custom sticky packet processing

## [2.0](https://github.com/xmaihh/Android-Serialport/tree/v2.0)
### Added
- Add support for setting parity, data bits, stop bits, flow control configuration items

## [1.0](https://github.com/xmaihh/Android-Serialport/tree/v1.0)
### Added
- Basic function, serial port set serial port number, baud rate, send and receive data


# FAQ

![Watch out](https://img.shields.io/badge/Watch%20out%20-FF4081)
This library does not provide ROOT permissions, please open the serial port '666' permissions yourself.
```
adb shell  chmod 666 /dev/ttyS1
```

## Contribute

Please do contribute! Issues and pull requests are welcome.

Thank you for your help improving software one changelog at a time!
