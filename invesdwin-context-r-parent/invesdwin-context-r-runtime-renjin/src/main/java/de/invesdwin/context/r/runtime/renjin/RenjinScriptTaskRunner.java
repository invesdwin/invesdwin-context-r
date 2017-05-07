package de.invesdwin.context.r.runtime.renjin;

import java.io.Reader;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.renjin.script.RenjinScriptEngine;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.context.r.runtime.contract.AScriptTask;
import de.invesdwin.context.r.runtime.renjin.pool.RenjinScriptEngineObjectPool;
import de.invesdwin.util.error.Throwables;

@Named
@Immutable
public final class RenjinScriptTaskRunner implements IScriptTaskRunner, FactoryBean<RenjinScriptTaskRunner> {

    public static final RenjinScriptTaskRunner INSTANCE = new RenjinScriptTaskRunner();

    private RenjinScriptTaskRunner() {}

    @Override
    public IScriptTaskResults run(final AScriptTask scriptTask) {
        //get session
        final RenjinScriptEngine renjinScriptEngine;
        try {
            renjinScriptEngine = RenjinScriptEngineObjectPool.INSTANCE.borrowObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        try (Reader reader = scriptTask.getScriptResourceAsReader()) {
            renjinScriptEngine.eval(reader);
            return new RenjinScriptTaskResults(renjinScriptEngine);
        } catch (final Throwable t) {
            try {
                RenjinScriptEngineObjectPool.INSTANCE.invalidateObject(renjinScriptEngine);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            throw Throwables.propagate(t);
        }
    }

    @Override
    public RenjinScriptTaskRunner getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RenjinScriptTaskRunner.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
