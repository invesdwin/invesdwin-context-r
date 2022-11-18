package de.invesdwin.context.r.runtime.renjin;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

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
    public RenjinScriptTaskRunnerR() {
    }

    @Override
    public <T> T run(final AScriptTaskR<T> scriptTask) {
        //get session
        final RenjinScriptEngine renjinScriptEngine = RenjinScriptEngineObjectPool.INSTANCE.borrowObject();
        try {
            //inputs
            final RenjinScriptTaskEngineR engine = new RenjinScriptTaskEngineR(renjinScriptEngine);
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            RenjinScriptEngineObjectPool.INSTANCE.returnObject(renjinScriptEngine);
            return result;
        } catch (final Throwable t) {
            RenjinScriptEngineObjectPool.INSTANCE.invalidateObject(renjinScriptEngine);
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
