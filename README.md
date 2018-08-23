[https://xmaihh.github.io/2018/08/23/Android串口通信/](https://xmaihh.github.io/2018/08/23/Android串口通信/)
利用Android studio 3.1上 CMake，将官方串口库移植过来,谷歌官方串口库的设置，仅支持串口名称及波特率
### 1.打开串口
```
	/*
	@param device 串口文件
	@param baudrate 比特率
	@param flags 标记 通常为0
	*/
public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException
	//ttyS2，比特率9600的串口打开方式如下
	SerialPort sp = new SerialPort(new File("/dev/ttyS2"), 9600, 0);
```
### 2.关闭串口
```
	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
```
### 3.发送数据
```
	mOutputStream = mSerialPort.getOutputStream();
    ...
	private class SendingThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) {
				try {
					if (mOutputStream != null) {
						mOutputStream.write(mBuffer);
					} else {
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
```
### 4.接收数据
```
	mOutputStream = mSerialPort.getOutputStream();
    ...
	private class SendingThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) {
				try {
					if (mOutputStream != null) {
						mOutputStream.write(mBuffer);
					} else {
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
```
[apk下载](https://github.com/xmaihh/SerialportDemo/blob/master/SerialportDemo.apk)
PC端调试工具[友善串口调试工具](https://github.com/xmaihh/SerialportDemo/blob/master/serial_port_utility_latest.exe)