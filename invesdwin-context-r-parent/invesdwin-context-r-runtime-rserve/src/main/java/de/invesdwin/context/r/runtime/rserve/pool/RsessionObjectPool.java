package de.invesdwin.context.r.runtime.rserve.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.math.R.Rsession;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.rserve.pool.internal.RsessionPoolableObjectFactory;
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
public final class RsessionObjectPool extends AObjectPool<ExtendedRserveSession>
        implements FactoryBean<RsessionObjectPool> {

    public static final RsessionObjectPool INSTANCE = new RsessionObjectPool();

    private final WrappedExecutorService timeoutMonitorExecutor = Executors
            .newFixedCallerRunsThreadPool(getClass().getSimpleName() + "_timeout", 1);
    @GuardedBy("this")
    private final NodeBufferingIterator<RsessionWrapper> rsessionRotation = new NodeBufferingIterator<RsessionWrapper>();

    private RsessionObjectPool() {
        super(RsessionPoolableObjectFactory.INSTANCE);
        timeoutMonitorExecutor.execute(new RsessionTimoutMonitor());
    }

    @Override
    protected synchronized ExtendedRserveSession internalBorrowObject() {
        if (rsessionRotation.isEmpty()) {
            return factory.makeObject();
        }
        final RsessionWrapper rsession = rsessionRotation.next();
        if (rsession != null) {
            return rsession.getRsession();
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public synchronized int getNumIdle() {
        return rsessionRotation.size();
    }

    @Override
    public synchronized Collection<ExtendedRserveSession> internalClear() {
        final Collection<ExtendedRserveSession> removed = new ArrayList<ExtendedRserveSession>();
        while (!rsessionRotation.isEmpty()) {
            removed.add(rsessionRotation.next().getRsession());
        }
        return removed;
    }

    @Override
    protected synchronized ExtendedRserveSession internalAddObject() {
        final ExtendedRserveSession pooled = factory.makeObject();
        rsessionRotation.add(new RsessionWrapper(factory.makeObject()));
        return pooled;
    }

    @Override
    protected synchronized void internalReturnObject(final ExtendedRserveSession obj) {
        rsessionRotation.add(new RsessionWrapper(obj));
    }

    @Override
    protected void internalInvalidateObject(final ExtendedRserveSession obj) {
        //Nothing happens
    }

    @Override
    protected synchronized void internalRemoveObject(final ExtendedRserveSession obj) {
        rsessionRotation.remove(new RsessionWrapper(obj));
    }

    private class RsessionTimoutMonitor implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Threads.throwIfInterrupted();
                    TimeUnit.MILLISECONDS.sleep(100);
                    synchronized (RsessionObjectPool.this) {
                        final ICloseableIterator<RsessionWrapper> iterator = rsessionRotation.iterator();
                        try {
                            while (true) {
                                final RsessionWrapper rsession = iterator.next();
                                if (rsession.isTimeoutExceeded()) {
                                    iterator.remove();
                                }
                            }
                        } catch (final NoSuchElementException e) {
                            //end reached
                        }
                    }
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static final class RsessionWrapper implements INode<RsessionWrapper> {

        private final ExtendedRserveSession rsession;
        private final FDate timeoutStart;
        private RsessionWrapper next;

        RsessionWrapper(final ExtendedRserveSession rsession) {
            this.rsession = rsession;
            this.timeoutStart = new FDate();
        }

        public ExtendedRserveSession getRsession() {
            return rsession;
        }

        public boolean isTimeoutExceeded() {
            return new Duration(timeoutStart, new FDate()).isGreaterThan(Duration.ONE_MINUTE);
        }

        @Override
        public int hashCode() {
            return rsession.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof RsessionWrapper) {
                final RsessionWrapper cObj = (RsessionWrapper) obj;
                return rsession.equals(cObj.getRsession());
            } else if (obj instanceof Rsession) {
                return rsession.equals(obj);
            } else {
                return false;
            }
        }

        @Override
        public RsessionWrapper getNext() {
            return next;
        }

        @Override
        public void setNext(final RsessionWrapper next) {
            this.next = next;
        }

    }

    @Override
    public RsessionObjectPool getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RsessionObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
