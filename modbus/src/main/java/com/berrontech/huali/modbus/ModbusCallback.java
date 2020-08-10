package com.berrontech.huali.modbus;

/**
 * Create by levent at 2020/8/10 17:19
 * ModbusCallback
 * Busbus operation callback
 *
 * @author levent
 */
public interface ModbusCallback<T> {
    void onSuccess(T result);

    void onFail(Throwable err, T result);

    void onComplete(Throwable err, T result);
}

