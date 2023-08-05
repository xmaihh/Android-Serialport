/*
 * Copyright 2009-2011 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//#include <termios.h>
#include <asm/termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "SerialPort.h"

#include "android/log.h"

static const char *TAG = "serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

/*
 * Class:     android_serialport_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_android_1serialport_1api_SerialPort_open
        (JNIEnv *env, jclass thiz, jstring path, jint baudrate, jint stopBits, jint dataBits,
         jint parity, jint flowCon, jint flags) {
    int fd;
    speed_t speed;
    jobject mFileDescriptor;

    /* Check arguments */
    {
        speed = baudrate;
        if (speed == -1) {
            /* TODO: throw an exception */
            LOGE("Invalid baudrate");
            return NULL;
        }
    }

    /* Opening device */
    {
        jboolean iscopy;
        const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
        LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
        fd = open(path_utf, O_RDWR | flags);
        LOGD("open() fd = %d", fd);
        (*env)->ReleaseStringUTFChars(env, path, path_utf);
        if (fd == -1) {
            /* Throw an exception */
            LOGE("Cannot open port");
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Configure device */
    {
        struct termios2 cfg;

        ioctl (fd, TCGETS2, &cfg);
        // Set baudrate
        cfg.c_cflag &= ~CBAUD;
        cfg.c_cflag |= BOTHER;

        cfg.c_ispeed = baudrate;
        cfg.c_ospeed = baudrate;

        cfg.c_cflag &= ~CSIZE;
        switch (dataBits) {
            case 5:
                cfg.c_cflag |= CS5;    //5 Data Bits
                break;
            case 6:
                cfg.c_cflag |= CS6;    //6 Data Bits
                break;
            case 7:
                cfg.c_cflag |= CS7;    //7 Data Bits
                break;
            case 8:
                cfg.c_cflag |= CS8;    //8 Data Bits
                break;
            default:
                cfg.c_cflag |= CS8;
                break;
        }

        switch (parity) {
            case 0:
                cfg.c_cflag &= ~PARENB;    //PARITY OFF
                break;
            case 1:
                cfg.c_cflag |= (PARODD | PARENB);   //PARITY ODD
                cfg.c_iflag &= ~IGNPAR;
                cfg.c_iflag |= PARMRK;
                cfg.c_iflag |= INPCK;
                break;
            case 2:
                cfg.c_iflag &= ~(IGNPAR | PARMRK); //PARITY EVEN
                cfg.c_iflag |= INPCK;
                cfg.c_cflag |= PARENB;
                cfg.c_cflag &= ~PARODD;
                break;
            case 3:
                //  PARITY SPACE
                cfg.c_iflag &= ~IGNPAR;             //  Make sure wrong parity is not ignored
                cfg.c_iflag |= PARMRK;              //  Marks parity error, parity error
                //  is given as three char sequence
                cfg.c_iflag |= INPCK;               //  Enable input parity checking
                cfg.c_cflag |= PARENB | CMSPAR;     //  Enable parity and set space parity
                cfg.c_cflag &= ~PARODD;             //
                break;
            case 4:
                //  PARITY MARK
                cfg.c_iflag &= ~IGNPAR;             //  Make sure wrong parity is not ignored
                cfg.c_iflag |= PARMRK;              //  Marks parity error, parity error
                //  is given as three char sequence
                cfg.c_iflag |= INPCK;               //  Enable input parity checking
                cfg.c_cflag |= PARENB | CMSPAR | PARODD;
                break;
            default:
                cfg.c_cflag &= ~PARENB;
                break;
        }

        switch (stopBits) {
            case 1:
                cfg.c_cflag &= ~CSTOPB;    //1 Stop Bit
                break;
            case 2:
                cfg.c_cflag |= CSTOPB;    //2 Stop Bits
                break;
            default:
                cfg.c_cflag &= ~CSTOPB;
                break;
        }

        // hardware flow control
        switch (flowCon) {
            case 0:
                cfg.c_cflag &= ~CRTSCTS;    //No Flow Control
                break;
            case 1:
                cfg.c_cflag |= CRTSCTS;    //Hardware Flow Control
                break;
            case 2:
                cfg.c_cflag |= IXON | IXOFF | IXANY;    //Software Flow Control
                break;
            default:
                cfg.c_cflag &= ~CRTSCTS;
                break;
        }


        if (ioctl(fd, TCSETS2, &cfg)) {
            LOGE("tcsets2() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Create a corresponding file descriptor */
    {
        jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
        jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
        jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
        mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
        (*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint) fd);
    }

    return mFileDescriptor;
}

/*
 * Class:     cedric_serial_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_1serialport_1api_SerialPort_close
        (JNIEnv *env, jobject thiz) {
    jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

    jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

    jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
    jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

    LOGD("close(fd = %d)", descriptor);
    close(descriptor);
}

