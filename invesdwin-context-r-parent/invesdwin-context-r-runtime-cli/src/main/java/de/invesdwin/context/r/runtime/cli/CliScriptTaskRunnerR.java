package de.invesdwin.context.r.runtime.cli;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.cli.pool.RCallerObjectPool;
import de.invesdwin.context.r.runtime.contract.AScriptTaskR;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class CliScriptTaskRunnerR implements IScriptTaskRunnerR, FactoryBean<CliScriptTaskRunnerR> {

    public static final CliScriptTaskRunnerR INSTANCE = new CliScriptTaskRunnerR();

    public static final String INTERNAL_RESULT_VARIABLE = CliScriptTaskRunnerR.class.getSimpleName() + "_result";

    /**
     * public for ServiceLoader support
     */
    public CliScriptTaskRunnerR() {}

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
            final CliScriptTaskInputsR inputs = new CliScriptTaskInputsR(rcaller);
            scriptTask.populateInputs(inputs);
            inputs.close();

            //execute
            rcaller.getRCode().addRCode(scriptTask.getScriptResourceAsString());
            rcaller.getRCode().addRCode(INTERNAL_RESULT_VARIABLE + " <- c()");
            rcaller.runAndReturnResultOnline(INTERNAL_RESULT_VARIABLE);

            //results
            final CliScriptTaskResultsR results = new CliScriptTaskResultsR(rcaller);
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
    public CliScriptTaskRunnerR getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return CliScriptTaskRunnerR.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
