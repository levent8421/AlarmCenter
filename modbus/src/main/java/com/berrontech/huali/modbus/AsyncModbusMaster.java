package com.berrontech.huali.modbus;

import androidx.annotation.NonNull;

import com.berrontech.huali.modbus.exception.ModbusInitException;
import com.berrontech.huali.modbus.exception.ModbusTransportException;
import com.berrontech.huali.modbus.msg.ModbusRequest;
import com.berrontech.huali.modbus.msg.ModbusResponse;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Create by levent at 2020/8/10 17:08
 * AsyncModbusMaster
 * Async Modbus Master
 *
 * @author levent
 */
public class AsyncModbusMaster implements ThreadFactory {
    private final BlockingQueue<Runnable> threadQueue = new LinkedBlockingDeque<>();
    private final ExecutorService threadPool =
            new ThreadPoolExecutor(1, 1,
                    0, TimeUnit.SECONDS,
                    threadQueue, this);
    private final ModbusMaster modbusMaster;

    public AsyncModbusMaster(ModbusMaster modbusMaster) {
        this.modbusMaster = modbusMaster;
    }

    public void init(final ModbusCallback<Void> callback) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Throwable err = null;
                try {
                    modbusMaster.init();
                    callback.onSuccess(null);
                } catch (ModbusInitException e) {
                    err = e;
                    callback.onFail(err, null);
                }
                callback.onComplete(err, null);
            }
        });
    }

    public void runOnInitialized(final Runnable r) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (modbusMaster.isInitialized()) {
                    r.run();
                }
            }
        });
    }

    public void destroyQuiet() {
        threadPool.execute(modbusMaster::destroy);
    }

    public ModbusResponse sendSync(ModbusRequest request) throws ModbusTransportException {
        return modbusMaster.send(request);
    }

    public void sendAsync(final ModbusRequest request, final ModbusCallback<ModbusResponse> callback) {
        threadPool.execute(() -> {
            Throwable err = null;
            ModbusResponse response = null;
            try {
                response = modbusMaster.send(request);
                callback.onSuccess(response);
            } catch (ModbusTransportException e) {
                err = e;
                callback.onFail(e, null);
            }
            callback.onComplete(err, response);
        });
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        return new Thread(r, "Modbus");
    }
}
