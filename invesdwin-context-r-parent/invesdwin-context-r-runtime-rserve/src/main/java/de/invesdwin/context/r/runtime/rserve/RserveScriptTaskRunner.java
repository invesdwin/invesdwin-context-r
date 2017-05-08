package de.invesdwin.context.r.runtime.rserve;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.AScriptTask;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.context.r.runtime.rserve.pool.RsessionObjectPool;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class RserveScriptTaskRunner implements IScriptTaskRunner, FactoryBean<RserveScriptTaskRunner> {

    public static final RserveScriptTaskRunner INSTANCE = new RserveScriptTaskRunner();

    /**
     * public for ServiceLoader support
     */
    public RserveScriptTaskRunner() {}

    @Override
    public <T> T run(final AScriptTask<T> scriptTask) {
        //get session
        final Rsession rsession;
        try {
            rsession = RsessionObjectPool.INSTANCE.borrowObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        try {
            //inputs
            final RserveScriptTaskInputs inputs = new RserveScriptTaskInputs(rsession);
            scriptTask.populateInputs(inputs);
            inputs.close();

            //execute
            eval(rsession, scriptTask.getScriptResourceAsString());

            //results
            final RserveScriptTaskResults results = new RserveScriptTaskResults(rsession);
            final T result = scriptTask.extractResults(results);
            results.close();

            //return
            RsessionObjectPool.INSTANCE.returnObject(rsession);
            return result;
        } catch (final Throwable t) {
            try {
                RsessionObjectPool.INSTANCE.invalidateObject(rsession);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            throw Throwables.propagate(t);
        }
    }

    public static void eval(final Rsession rsession, final String expression) {
        final REXP eval = rsession.eval(expression);
        if (eval == null) {
            throw new IllegalStateException(
                    String.valueOf(de.invesdwin.context.r.runtime.rserve.pool.internal.RsessionLogger.get(rsession)
                            .getErrorMessage()));
        }
    }

    @Override
    public RserveScriptTaskRunner getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RserveScriptTaskRunner.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
