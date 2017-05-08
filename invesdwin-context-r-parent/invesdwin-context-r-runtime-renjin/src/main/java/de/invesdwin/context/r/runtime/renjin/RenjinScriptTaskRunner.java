package de.invesdwin.context.r.runtime.renjin;

import java.io.Reader;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.renjin.script.RenjinScriptEngine;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.AScriptTask;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.context.r.runtime.renjin.pool.RenjinScriptEngineObjectPool;
import de.invesdwin.util.error.Throwables;

@Named
@Immutable
public final class RenjinScriptTaskRunner implements IScriptTaskRunner, FactoryBean<RenjinScriptTaskRunner> {

    public static final RenjinScriptTaskRunner INSTANCE = new RenjinScriptTaskRunner();

    /**
     * public for ServiceLoader support
     */
    public RenjinScriptTaskRunner() {}

    @Override
    public <T> T run(final AScriptTask<T> scriptTask) {
        //get session
        final RenjinScriptEngine renjinScriptEngine;
        try {
            renjinScriptEngine = RenjinScriptEngineObjectPool.INSTANCE.borrowObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        try {
            //inputs
            final RenjinScriptTaskInputs inputs = new RenjinScriptTaskInputs(renjinScriptEngine);
            scriptTask.populateInputs(inputs);
            inputs.close();

            //execute
            try (Reader reader = scriptTask.getScriptResourceAsReader()) {
                renjinScriptEngine.eval(reader);
            }

            //results
            final RenjinScriptTaskResults results = new RenjinScriptTaskResults(renjinScriptEngine);
            final T result = scriptTask.extractResults(results);
            results.close();

            //return
            RenjinScriptEngineObjectPool.INSTANCE.returnObject(renjinScriptEngine);
            return result;
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
