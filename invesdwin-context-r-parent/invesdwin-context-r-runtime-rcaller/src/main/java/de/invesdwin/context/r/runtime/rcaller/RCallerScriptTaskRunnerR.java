package de.invesdwin.context.r.runtime.rcaller;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.contract.AScriptTaskR;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.rcaller.pool.RCallerObjectPool;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class RCallerScriptTaskRunnerR implements IScriptTaskRunnerR, FactoryBean<RCallerScriptTaskRunnerR> {

    public static final RCallerScriptTaskRunnerR INSTANCE = new RCallerScriptTaskRunnerR();

    public static final String INTERNAL_RESULT_VARIABLE = RCallerScriptTaskRunnerR.class.getSimpleName() + "_result";

    /**
     * public for ServiceLoader support
     */
    public RCallerScriptTaskRunnerR() {}

    @Override
    public <T> T run(final AScriptTaskR<T> scriptTask) {
        //get session
        final RCaller rcaller = RCallerObjectPool.INSTANCE.borrowObject();
        try {
            //inputs
            rcaller.getRCode().clearOnline();
            final RCallerScriptTaskEngineR engine = new RCallerScriptTaskEngineR(rcaller);
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            RCallerObjectPool.INSTANCE.returnObject(rcaller);
            return result;
        } catch (final Throwable t) {
            RCallerObjectPool.INSTANCE.invalidateObject(rcaller);
            throw Throwables.propagate(t);
        }
    }

    @Override
    public RCallerScriptTaskRunnerR getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RCallerScriptTaskRunnerR.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
