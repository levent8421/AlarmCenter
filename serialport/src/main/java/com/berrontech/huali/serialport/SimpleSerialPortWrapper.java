package com.berrontech.huali.serialport;

import com.berrontech.huali.modbus.serial.SerialPortWrapper;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * Create by levent at 2020/8/10 18:25
 * SimpleSerialPortWrapper
 * Simple SerialPort Wrapper
 *
 * @author levent
 */
public class SimpleSerialPortWrapper implements SerialPortWrapper {
    private static final int OPEN_SERIAL_FLAG = 0;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = 1;
    private static final int PARITY = 0;
    private SerialPort serialPort;
    private File deviceFile;
    private int baudRate;

    public SimpleSerialPortWrapper(File deviceFile, int baudRate) {
        this.deviceFile = deviceFile;
        this.baudRate = baudRate;
    }

    @Override
    public void close() throws Exception {
        serialPort.close();
    }

    @Override
    public synchronized void open() throws Exception {
        if (serialPort != null) {
            serialPort.close();
        }
        serialPort = null;
        serialPort = new SerialPort(deviceFile, baudRate, OPEN_SERIAL_FLAG);
    }

    @Override
    public InputStream getInputStream() {
        if (serialPort == null) {
            return null;
        }
        return serialPort.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        if (serialPort == null) {
            return null;
        }
        return serialPort.getOutputStream();
    }

    @Override
    public int getBaudRate() {
        return baudRate;
    }

    @Override
    public int getFlowControlIn() {
        return 0;
    }

    @Override
    public int getFlowControlOut() {
        return 0;
    }

    @Override
    public int getDataBits() {
        return DATA_BITS;
    }

    @Override
    public int getStopBits() {
        return STOP_BITS;
    }

    @Override
    public int getParity() {
        return PARITY;
    }
}
