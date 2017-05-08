package de.invesdwin.context.r.runtime.cli;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.cli.pool.RCallerObjectPool;
import de.invesdwin.context.r.runtime.contract.AScriptTask;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class CliScriptTaskRunner implements IScriptTaskRunner, FactoryBean<CliScriptTaskRunner> {

    public static final CliScriptTaskRunner INSTANCE = new CliScriptTaskRunner();

    public static final String INTERNAL_RESULT_VARIABLE = CliScriptTaskRunner.class.getSimpleName() + "_result";

    /**
     * public for ServiceLoader support
     */
    public CliScriptTaskRunner() {}

    @Override
    public <T> T run(final AScriptTask<T> scriptTask) {
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
            final CliScriptTaskInputs inputs = new CliScriptTaskInputs(rcaller);
            scriptTask.populateInputs(inputs);
            inputs.close();

            //execute
            rcaller.getRCode().addRCode(scriptTask.getScriptResourceAsString());
            rcaller.getRCode().addRCode(INTERNAL_RESULT_VARIABLE + " <- c()");
            rcaller.runAndReturnResultOnline(INTERNAL_RESULT_VARIABLE);

            //results
            final CliScriptTaskResults results = new CliScriptTaskResults(rcaller);
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
    public CliScriptTaskRunner getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return CliScriptTaskRunner.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
