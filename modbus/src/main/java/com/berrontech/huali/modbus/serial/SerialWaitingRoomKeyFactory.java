package com.berrontech.huali.modbus.serial;

import com.berrontech.huali.modbus.sero.messaging.IncomingResponseMessage;
import com.berrontech.huali.modbus.sero.messaging.OutgoingRequestMessage;
import com.berrontech.huali.modbus.sero.messaging.WaitingRoomKey;
import com.berrontech.huali.modbus.sero.messaging.WaitingRoomKeyFactory;

public class SerialWaitingRoomKeyFactory implements WaitingRoomKeyFactory {
    private static final Sync sync = new Sync();

    @Override
    public WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request) {
        return sync;
    }

    @Override
    public WaitingRoomKey createWaitingRoomKey(IncomingResponseMessage response) {
        return sync;
    }

    static class Sync implements WaitingRoomKey {
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return true;
        }
    }
}
