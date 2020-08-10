/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2006-2011 Serotonin Software Technologies Inc. http://serotoninsoftware.com
 * @author Matthew Lohbihler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.berrontech.huali.modbus.ip.encap;

import com.berrontech.huali.modbus.base.ModbusUtils;
import com.berrontech.huali.modbus.exception.ModbusTransportException;
import com.berrontech.huali.modbus.msg.ModbusRequest;
import com.berrontech.huali.modbus.sero.messaging.IncomingRequestMessage;
import com.berrontech.huali.modbus.sero.messaging.OutgoingRequestMessage;
import com.berrontech.huali.modbus.sero.util.queue.ByteQueue;

public class EncapMessageRequest extends EncapMessage implements OutgoingRequestMessage, IncomingRequestMessage {
    static EncapMessageRequest createEncapMessageRequest(ByteQueue queue) throws ModbusTransportException {
        // Create the modbus response.
        ModbusRequest request = ModbusRequest.createModbusRequest(queue);
        EncapMessageRequest encapRequest = new EncapMessageRequest(request);

        // Check the CRC
        ModbusUtils.checkCRC(encapRequest.modbusMessage, queue);

        return encapRequest;
    }

    public EncapMessageRequest(ModbusRequest modbusRequest) {
        super(modbusRequest);
    }

    @Override
    public boolean expectsResponse() {
        return modbusMessage.getSlaveId() != 0;
    }

    public ModbusRequest getModbusRequest() {
        return (ModbusRequest) modbusMessage;
    }
}
