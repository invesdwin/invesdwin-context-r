package de.invesdwin.context.r.runtime.rcaller.pool.internal;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.rcaller.RCallerScriptTaskRunnerR;
import de.invesdwin.util.concurrent.pool.IPoolableObjectFactory;

@ThreadSafe
@Named
public final class RCallerPoolableObjectFactory
        implements IPoolableObjectFactory<RCaller>, FactoryBean<RCallerPoolableObjectFactory> {

    public static final RCallerPoolableObjectFactory INSTANCE = new RCallerPoolableObjectFactory();

    private RCallerPoolableObjectFactory() {}

    @Override
    public RCaller makeObject() {
        return new ModifiedRCaller();
    }

    @Override
    public void destroyObject(final RCaller obj) {
        obj.StopRCallerOnline();
    }

    @Override
    public boolean validateObject(final RCaller obj) {
        return true;
    }

    @Override
    public void activateObject(final RCaller obj) {}

    @Override
    public void passivateObject(final RCaller obj) {
        obj.getRCode().clear();
        obj.getRCode().getCode().insert(0, IScriptTaskRunnerR.CLEANUP_SCRIPT + "\n");
        obj.getRCode().addRCode(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE + " <- c()");
        obj.runAndReturnResultOnline(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE);
        obj.deleteTempFiles();
    }

    @Override
    public RCallerPoolableObjectFactory getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RCallerPoolableObjectFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
