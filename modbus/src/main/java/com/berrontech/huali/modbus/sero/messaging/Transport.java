package com.berrontech.huali.modbus.sero.messaging;

import java.io.IOException;

/**
 * A transport is a wrapper around the means by which data is transferred. So, there could be transports for serial
 * ports, sockets, UDP, email, etc.
 *
 * @author Matthew Lohbihler
 */
public interface Transport {
    void setConsumer(DataConsumer consumer) throws IOException;

    void removeConsumer();

    void write(byte[] data) throws IOException;

    void write(byte[] data, int len) throws IOException;
}
