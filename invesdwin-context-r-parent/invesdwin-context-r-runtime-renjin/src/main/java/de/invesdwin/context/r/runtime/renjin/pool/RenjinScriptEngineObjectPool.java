package de.invesdwin.context.r.runtime.renjin.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.renjin.script.RenjinScriptEngine;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.pool.AObjectPool;
import de.invesdwin.context.r.runtime.renjin.pool.internal.RenjinScriptEnginePoolableObjectFactory;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;

@ThreadSafe
@Named
public final class RenjinScriptEngineObjectPool extends AObjectPool<RenjinScriptEngine>
        implements FactoryBean<RenjinScriptEngineObjectPool> {

    public static final RenjinScriptEngineObjectPool INSTANCE = new RenjinScriptEngineObjectPool();

    private final WrappedExecutorService proxyCooldownMonitorExecutor = Executors
            .newFixedCallerRunsThreadPool(getClass().getSimpleName() + "_timeout", 1);
    @GuardedBy("this")
    private final List<RenjinScriptEngineWrapper> renjinScriptEngineRotation = new ArrayList<RenjinScriptEngineWrapper>();

    private RenjinScriptEngineObjectPool() {
        super(RenjinScriptEnginePoolableObjectFactory.INSTANCE);
        proxyCooldownMonitorExecutor.execute(new RenjinScriptEngineTimoutMonitor());
    }

    @Override
    protected synchronized RenjinScriptEngine internalBorrowObject() {
        if (renjinScriptEngineRotation.isEmpty()) {
            return factory.makeObject();
        }
        final RenjinScriptEngineWrapper renjinScriptEngine = renjinScriptEngineRotation.remove(0);
        if (renjinScriptEngine != null) {
            return renjinScriptEngine.getRenjinScriptEngine();
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public synchronized int getNumIdle() {
        return renjinScriptEngineRotation.size();
    }

    @Override
    public synchronized Collection<RenjinScriptEngine> internalClear() {
        final Collection<RenjinScriptEngine> removed = new ArrayList<RenjinScriptEngine>();
        while (!renjinScriptEngineRotation.isEmpty()) {
            removed.add(renjinScriptEngineRotation.remove(0).getRenjinScriptEngine());
        }
        return removed;
    }

    @Override
    protected synchronized RenjinScriptEngine internalAddObject() {
        final RenjinScriptEngine pooled = factory.makeObject();
        renjinScriptEngineRotation.add(new RenjinScriptEngineWrapper(factory.makeObject()));
        return pooled;
    }

    @Override
    protected synchronized void internalReturnObject(final RenjinScriptEngine obj) {
        renjinScriptEngineRotation.add(new RenjinScriptEngineWrapper(obj));
    }

    @Override
    protected void internalInvalidateObject(final RenjinScriptEngine obj) {
        //Nothing happens
    }

    @Override
    protected synchronized void internalRemoveObject(final RenjinScriptEngine obj) {
        renjinScriptEngineRotation.remove(new RenjinScriptEngineWrapper(obj));
    }

    private class RenjinScriptEngineTimoutMonitor implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Threads.throwIfInterrupted();
                    TimeUnit.MILLISECONDS.sleep(100);
                    synchronized (RenjinScriptEngineObjectPool.this) {
                        if (!renjinScriptEngineRotation.isEmpty()) {
                            final List<RenjinScriptEngineWrapper> copy = new ArrayList<RenjinScriptEngineWrapper>(
                                    renjinScriptEngineRotation);
                            for (final RenjinScriptEngineWrapper renjinScriptEngine : copy) {
                                if (renjinScriptEngine.isTimeoutExceeded()) {
                                    Assertions.assertThat(renjinScriptEngineRotation.remove(renjinScriptEngine))
                                            .isTrue();
                                }
                            }
                        }
                    }
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static final class RenjinScriptEngineWrapper {

        private final RenjinScriptEngine renjinScriptEngine;
        private final FDate timeoutStart;

        RenjinScriptEngineWrapper(final RenjinScriptEngine rCaller) {
            this.renjinScriptEngine = rCaller;
            this.timeoutStart = new FDate();
        }

        public RenjinScriptEngine getRenjinScriptEngine() {
            return renjinScriptEngine;
        }

        public boolean isTimeoutExceeded() {
            return new Duration(timeoutStart, new FDate()).isGreaterThan(Duration.ONE_MINUTE);
        }

        @Override
        public int hashCode() {
            return renjinScriptEngine.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof RenjinScriptEngineWrapper) {
                final RenjinScriptEngineWrapper cObj = (RenjinScriptEngineWrapper) obj;
                return renjinScriptEngine.equals(cObj.getRenjinScriptEngine());
            } else if (obj instanceof RenjinScriptEngine) {
                return renjinScriptEngine.equals(obj);
            } else {
                return false;
            }
        }

    }

    @Override
    public RenjinScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RenjinScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
