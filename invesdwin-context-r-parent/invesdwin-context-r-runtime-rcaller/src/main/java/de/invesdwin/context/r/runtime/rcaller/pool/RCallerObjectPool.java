package de.invesdwin.context.r.runtime.rcaller.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.rcaller.pool.internal.RCallerPoolableObjectFactory;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.pool.AObjectPool;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class RCallerObjectPool extends AObjectPool<RCaller> implements FactoryBean<RCallerObjectPool> {

    public static final RCallerObjectPool INSTANCE = new RCallerObjectPool();

    private final WrappedExecutorService timeoutMonitorExecutor = Executors
            .newFixedCallerRunsThreadPool(getClass().getSimpleName() + "_timeout", 1);
    @GuardedBy("this")
    private final NodeBufferingIterator<RCallerWrapper> rCallerRotation = new NodeBufferingIterator<RCallerWrapper>();

    private RCallerObjectPool() {
        super(RCallerPoolableObjectFactory.INSTANCE);
        timeoutMonitorExecutor.execute(new RCallerTimoutMonitor());
    }

    @Override
    protected synchronized RCaller internalBorrowObject() {
        if (rCallerRotation.isEmpty()) {
            return factory.makeObject();
        }
        final RCallerWrapper rCaller = rCallerRotation.next();
        if (rCaller != null) {
            return rCaller.getRCaller();
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public synchronized int getNumIdle() {
        return rCallerRotation.size();
    }

    @Override
    public synchronized Collection<RCaller> internalClear() {
        final Collection<RCaller> removed = new ArrayList<RCaller>();
        while (!rCallerRotation.isEmpty()) {
            removed.add(rCallerRotation.next().getRCaller());
        }
        return removed;
    }

    @Override
    protected synchronized RCaller internalAddObject() {
        final RCaller pooled = factory.makeObject();
        rCallerRotation.add(new RCallerWrapper(factory.makeObject()));
        return pooled;
    }

    @Override
    protected synchronized void internalReturnObject(final RCaller obj) {
        rCallerRotation.add(new RCallerWrapper(obj));
    }

    @Override
    protected void internalInvalidateObject(final RCaller obj) {
        //Nothing happens
    }

    @Override
    protected synchronized void internalRemoveObject(final RCaller obj) {
        rCallerRotation.remove(new RCallerWrapper(obj));
    }

    private class RCallerTimoutMonitor implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Threads.throwIfInterrupted();
                    TimeUnit.MILLISECONDS.sleep(100);
                    synchronized (RCallerObjectPool.this) {
                        if (!rCallerRotation.isEmpty()) {
                            final ICloseableIterator<RCallerWrapper> iterator = rCallerRotation.iterator();
                            try {
                                while (true) {
                                    final RCallerWrapper rCaller = iterator.next();
                                    if (rCaller.isTimeoutExceeded()) {
                                        iterator.remove();
                                    }
                                }
                            } catch (final NoSuchElementException e) {
                                //end reached
                            }
                        }
                    }
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static final class RCallerWrapper implements INode<RCallerWrapper> {

        private final RCaller rCaller;
        private final FDate timeoutStart;
        private RCallerWrapper next;

        RCallerWrapper(final RCaller rCaller) {
            this.rCaller = rCaller;
            this.timeoutStart = new FDate();
        }

        public RCaller getRCaller() {
            return rCaller;
        }

        public boolean isTimeoutExceeded() {
            return new Duration(timeoutStart, new FDate()).isGreaterThan(Duration.ONE_MINUTE);
        }

        @Override
        public int hashCode() {
            return rCaller.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof RCallerWrapper) {
                final RCallerWrapper cObj = (RCallerWrapper) obj;
                return rCaller.equals(cObj.getRCaller());
            } else if (obj instanceof RCaller) {
                return rCaller.equals(obj);
            } else {
                return false;
            }
        }

        @Override
        public RCallerWrapper getNext() {
            return next;
        }

        @Override
        public void setNext(final RCallerWrapper next) {
            this.next = next;
        }

    }

    @Override
    public RCallerObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RCallerObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
