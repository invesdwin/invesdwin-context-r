package de.invesdwin.context.r.runtime.renjin;

import java.io.Reader;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.renjin.script.RenjinScriptEngine;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.AScriptTaskR;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.renjin.pool.RenjinScriptEngineObjectPool;
import de.invesdwin.util.error.Throwables;

@Named
@Immutable
public final class RenjinScriptTaskRunnerR implements IScriptTaskRunnerR, FactoryBean<RenjinScriptTaskRunnerR> {

    public static final RenjinScriptTaskRunnerR INSTANCE = new RenjinScriptTaskRunnerR();

    /**
     * public for ServiceLoader support
     */
    public RenjinScriptTaskRunnerR() {}

    @Override
    public <T> T run(final AScriptTaskR<T> scriptTask) {
        //get session
        final RenjinScriptEngine renjinScriptEngine;
        try {
            renjinScriptEngine = RenjinScriptEngineObjectPool.INSTANCE.borrowObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        try {
            //inputs
            final RenjinScriptTaskInputsR inputs = new RenjinScriptTaskInputsR(renjinScriptEngine);
            scriptTask.populateInputs(inputs);
            inputs.close();

            //execute
            try (Reader reader = scriptTask.getScriptResourceAsReader()) {
                renjinScriptEngine.eval(reader);
            }

            //results
            final RenjinScriptTaskResultsR results = new RenjinScriptTaskResultsR(renjinScriptEngine);
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
    public RenjinScriptTaskRunnerR getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RenjinScriptTaskRunnerR.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
