package com.berrontech.huali.modbus.requset;

import android.util.Log;

import com.berrontech.huali.modbus.ModbusFactory;
import com.berrontech.huali.modbus.ModbusMaster;
import com.berrontech.huali.modbus.exception.ModbusInitException;
import com.berrontech.huali.modbus.exception.ModbusTransportException;
import com.berrontech.huali.modbus.ip.IpParameters;
import com.berrontech.huali.modbus.msg.ReadCoilsRequest;
import com.berrontech.huali.modbus.msg.ReadCoilsResponse;
import com.berrontech.huali.modbus.msg.ReadDiscreteInputsRequest;
import com.berrontech.huali.modbus.msg.ReadDiscreteInputsResponse;
import com.berrontech.huali.modbus.msg.ReadExceptionStatusRequest;
import com.berrontech.huali.modbus.msg.ReadExceptionStatusResponse;
import com.berrontech.huali.modbus.msg.ReadHoldingRegistersRequest;
import com.berrontech.huali.modbus.msg.ReadHoldingRegistersResponse;
import com.berrontech.huali.modbus.msg.ReadInputRegistersRequest;
import com.berrontech.huali.modbus.msg.ReadInputRegistersResponse;
import com.berrontech.huali.modbus.msg.ReportSlaveIdRequest;
import com.berrontech.huali.modbus.msg.ReportSlaveIdResponse;
import com.berrontech.huali.modbus.msg.WriteCoilRequest;
import com.berrontech.huali.modbus.msg.WriteCoilResponse;
import com.berrontech.huali.modbus.msg.WriteCoilsRequest;
import com.berrontech.huali.modbus.msg.WriteCoilsResponse;
import com.berrontech.huali.modbus.msg.WriteMaskRegisterRequest;
import com.berrontech.huali.modbus.msg.WriteMaskRegisterResponse;
import com.berrontech.huali.modbus.msg.WriteRegisterRequest;
import com.berrontech.huali.modbus.msg.WriteRegisterResponse;
import com.berrontech.huali.modbus.msg.WriteRegistersRequest;
import com.berrontech.huali.modbus.msg.WriteRegistersResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 创建者   zgkxzx
 * 创建日期 2017/6/12.
 * 功能描述
 */

public class ModbusReq {
    private final static String TAG = ModbusReq.class.getSimpleName();

    private static ModbusReq modbusReq;
    private ModbusMaster mModbusMaster;
    private ModbusParam modbusParam = new ModbusParam();

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    private boolean isInit = false;

    private ModbusReq() {

    }

    /**
     * get modbus instance
     *
     * @return
     */
    public static synchronized ModbusReq getInstance() {
        if (modbusReq == null)
            modbusReq = new ModbusReq();
        return modbusReq;
    }

    /**
     * set modbus param
     *
     * @param modbusParam
     */
    public ModbusReq setParam(ModbusParam modbusParam) {
        this.modbusParam = modbusParam;
        return modbusReq;
    }

    /**
     * init modbus
     *
     * @throws ModbusInitException
     */
    public void init(final OnRequestBack<String> onRequestBack) {
        ModbusFactory mModbusFactory = new ModbusFactory();
        IpParameters params = new IpParameters();

        params.setHost(modbusParam.host);
        params.setPort(modbusParam.port);
        params.setEncapsulated(modbusParam.encapsulated);

        mModbusMaster = mModbusFactory.createTcpMaster(params, modbusParam.keepAlive);
        mModbusMaster.setRetries(modbusParam.retries);
        mModbusMaster.setTimeout(modbusParam.timeout);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mModbusMaster.init();
                } catch (ModbusInitException e) {
                    mModbusMaster.destroy();
                    isInit = false;
                    Log.d(TAG, "Modbus4Android init failed " + e);
                    onRequestBack.onFailed("Modbus4Android init failed ");
                    return;
                }
                Log.d(TAG, "Modbus4Android init success");
                isInit = true;
                onRequestBack.onSuccess("Modbus4Android init success");

            }
        });

    }

    /**
     * destory the modbus4Android instance
     */
    public void destory() {
        modbusReq = null;
        mModbusMaster.destroy();
        isInit = false;
    }


    /**
     * Function Code 1
     * Read Coil Register
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param len           length
     */
    public void readCoil(final OnRequestBack<boolean[]> onRequestBack, final int slaveId, final int start, final int len) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    ReadCoilsRequest request = new ReadCoilsRequest(slaveId, start, len);
                    ReadCoilsResponse response = (ReadCoilsResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else {
                        boolean[] booleanData = response.getBooleanData();
                        boolean[] resultByte = new boolean[len];
                        System.arraycopy(booleanData, 0, resultByte, 0, len);
                        onRequestBack.onSuccess(resultByte);
                    }

                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }

    /**
     * Function Code 2
     * Read DiscreteInput Register
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param len           length
     */
    public void readDiscreteInput(final OnRequestBack<boolean[]> onRequestBack, final int slaveId, final int start, final int len) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }


        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveId, start, len);
                    ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else {
                        boolean[] booleanData = response.getBooleanData();
                        boolean[] resultByte = new boolean[len];
                        System.arraycopy(booleanData, 0, resultByte, 0, len);
                        onRequestBack.onSuccess(resultByte);
                    }
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }

    /**
     * Function Code 3
     * Read Holding Registers
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param len           length
     */
    public void readHoldingRegisters(final OnRequestBack<short[]> onRequestBack, final int slaveId, final int start, final int len) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, len);
                    ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else
                        onRequestBack.onSuccess(response.getShortData());
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }

    /**
     * Function Code 4
     * Read Input Registers
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param len           length
     */
    public void readInputRegisters(final OnRequestBack<short[]> onRequestBack, final int slaveId, final int start, final int len) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    ReadInputRegistersRequest request = new ReadInputRegistersRequest(slaveId, start, len);
                    ReadInputRegistersResponse response = (ReadInputRegistersResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else
                        onRequestBack.onSuccess(response.getShortData());
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }

    /**
     * Function Code 5
     * Write Coil
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param offset        offset address
     * @param value         value
     */
    public void writeCoil(final OnRequestBack<String> onRequestBack, final int slaveId, final int offset, final boolean value) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    WriteCoilRequest request = new WriteCoilRequest(slaveId, offset, value);
                    WriteCoilResponse response = (WriteCoilResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else
                        onRequestBack.onSuccess("Success");
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }

    /**
     * Function Code 15
     * Write Coils
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param values        values
     */
    public void writeCoils(final OnRequestBack<String> onRequestBack, final int slaveId, final int start, final boolean[] values) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    WriteCoilsRequest request = new WriteCoilsRequest(slaveId, start, values);
                    WriteCoilsResponse response = (WriteCoilsResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else
                        onRequestBack.onSuccess("Success");
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }

    /**
     * Function Code 6
     * Write Register
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param offset        offset address
     * @param value         value
     */
    public void writeRegister(final OnRequestBack<String> onRequestBack, final int slaveId, final int offset, final int value) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    WriteRegisterRequest request = new WriteRegisterRequest(slaveId, offset, value);
                    WriteRegisterResponse response = (WriteRegisterResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else
                        onRequestBack.onSuccess("Success");
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }


    /**
     * Function Code 16
     * Write Registers
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param values        values
     */
    public void writeRegisters(final OnRequestBack<String> onRequestBack, final int slaveId, final int start, final short[] values) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    WriteRegistersRequest request = new WriteRegistersRequest(slaveId, start, values);
                    WriteRegistersResponse response = (WriteRegistersResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else
                        onRequestBack.onSuccess("Success");
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }

    /**
     * Function Code 7
     * Read Exceptioin Status
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     */
    public void readExceptionStatus(final OnRequestBack<Byte> onRequestBack, final int slaveId) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    ReadExceptionStatusRequest request = new ReadExceptionStatusRequest(slaveId);
                    ReadExceptionStatusResponse response = (ReadExceptionStatusResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else
                        onRequestBack.onSuccess(response.getExceptionStatus());
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }

    /**
     * Function Code 17
     * Report Slave Id
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     */
    public void reportSlaveId(final OnRequestBack<byte[]> onRequestBack, final int slaveId) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    ReportSlaveIdRequest request = new ReportSlaveIdRequest(slaveId);
                    ReportSlaveIdResponse response = (ReportSlaveIdResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else
                        onRequestBack.onSuccess(response.getData());
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }

    /**
     * Function Code 22
     * Mask Write 4X Register
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param offset        offset address
     * @param and           and
     * @param and           or
     */
    public void writeMaskRegister(final OnRequestBack<String> onRequestBack, final int slaveId, final int offset, final int and, final int or) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    WriteMaskRegisterRequest request = new WriteMaskRegisterRequest(slaveId, offset, and, or);
                    WriteMaskRegisterResponse response = (WriteMaskRegisterResponse) mModbusMaster.send(request);

                    if (response.isException())
                        onRequestBack.onFailed(response.getExceptionMessage());
                    else
                        onRequestBack.onSuccess("Success");
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    onRequestBack.onFailed(e.toString());
                }
            }
        });

    }
}
