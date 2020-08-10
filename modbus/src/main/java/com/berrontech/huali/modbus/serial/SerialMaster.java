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
package com.berrontech.huali.modbus.serial;

import com.berrontech.huali.modbus.ModbusMaster;
import com.berrontech.huali.modbus.exception.ModbusInitException;
import com.berrontech.huali.modbus.sero.messaging.EpollStreamTransportCharSpaced;
import com.berrontech.huali.modbus.sero.messaging.StreamTransportCharSpaced;
import com.berrontech.huali.modbus.sero.messaging.Transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract public class SerialMaster extends ModbusMaster {


    private final Log LOG = LogFactory.getLog(SerialMaster.class);
    //These options are no longer supported as they were originally a hack that didn't work right anyway
    @Deprecated
    public static final int SYNC_TRANSPORT = 1;
    @Deprecated
    public static final int SYNC_SLAVE = 2;
    @Deprecated
    public static final int SYNC_FUNCTION = 3;

    //
    // Configuration fields.
    protected long characterSpacing; //Time in ns

    // Runtime fields.
    protected SerialPortWrapper wrapper;
    protected Transport transport;


    public SerialMaster(SerialPortWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void init() throws ModbusInitException {
        try {

            this.wrapper.open();

            if (getePoll() != null)
                transport = new EpollStreamTransportCharSpaced(wrapper.getInputStream(), wrapper.getOutputStream(),
                        getePoll(), this.characterSpacing);
            else
                transport = new StreamTransportCharSpaced(wrapper.getInputStream(), wrapper.getOutputStream(), this.characterSpacing);
        } catch (Exception e) {
            throw new ModbusInitException(e);
        }
    }

    public void close() {
        try {
            wrapper.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
