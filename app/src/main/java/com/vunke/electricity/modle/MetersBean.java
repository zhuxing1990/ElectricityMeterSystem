package com.vunke.electricity.modle;

import com.example.x6.serialportlib.SerialPort;
import com.vunke.electricity.db.Meter;

/**
 * Created by zhuxi on 2019/9/20.
 */

public class MetersBean {
    private Meter meter;
    private SerialPort serialPort;

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public String toString() {
        return "MetersBean{" +
                "meter=" + meter +
                '}';
    }
}
