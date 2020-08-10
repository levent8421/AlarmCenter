package com.berrontech.huali.modbus.sero.messaging;

import java.io.IOException;

public class TimeoutException extends IOException {
    private static final long serialVersionUID = 1L;

    public TimeoutException(String message) {
        super(message);
    }
}
