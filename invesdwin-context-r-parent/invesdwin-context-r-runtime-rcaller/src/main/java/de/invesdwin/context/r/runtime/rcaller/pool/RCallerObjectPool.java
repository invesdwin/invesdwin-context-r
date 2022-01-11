package de.invesdwin.context.r.runtime.rcaller.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.rcaller.RCallerScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.rcaller.pool.internal.ModifiedRCaller;
import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class RCallerObjectPool extends ATimeoutObjectPool<RCaller> implements FactoryBean<RCallerObjectPool> {

    public static final RCallerObjectPool INSTANCE = new RCallerObjectPool();

    private RCallerObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final RCaller obj) {
        obj.StopRCallerOnline();
    }

    @Override
    protected RCaller newObject() {
        return new ModifiedRCaller();
    }

    @Override
    protected void passivateObject(final RCaller obj) {
        obj.getRCode().clear();
        obj.getRCode().getCode().insert(0, IScriptTaskRunnerR.CLEANUP_SCRIPT + "\n");
        obj.getRCode().addRCode(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE + " <- c()");
        obj.runAndReturnResultOnline(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE);
        obj.deleteTempFiles();
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
