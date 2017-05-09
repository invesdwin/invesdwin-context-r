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
        final RCaller rcaller;
        try {
            rcaller = RCallerObjectPool.INSTANCE.borrowObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        try {
            //inputs
            rcaller.getRCode().clearOnline();
            final RCallerScriptTaskInputsR inputs = new RCallerScriptTaskInputsR(rcaller);
            scriptTask.populateInputs(inputs);
            inputs.close();

            //execute
            rcaller.getRCode().addRCode(scriptTask.getScriptResourceAsString());
            rcaller.getRCode().addRCode(INTERNAL_RESULT_VARIABLE + " <- c()");
            rcaller.runAndReturnResultOnline(INTERNAL_RESULT_VARIABLE);

            //results
            final RCallerScriptTaskResultsR results = new RCallerScriptTaskResultsR(rcaller);
            final T result = scriptTask.extractResults(results);
            results.close();

            //return
            RCallerObjectPool.INSTANCE.returnObject(rcaller);
            return result;
        } catch (final Throwable t) {
            try {
                RCallerObjectPool.INSTANCE.invalidateObject(rcaller);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
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
