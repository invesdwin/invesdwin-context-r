package de.invesdwin.context.r.runtime.rserve;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.AScriptTaskR;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.rserve.pool.RsessionObjectPool;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class RserveScriptTaskRunnerR implements IScriptTaskRunnerR, FactoryBean<RserveScriptTaskRunnerR> {

    public static final RserveScriptTaskRunnerR INSTANCE = new RserveScriptTaskRunnerR();

    /**
     * public for ServiceLoader support
     */
    public RserveScriptTaskRunnerR() {}

    @Override
    public <T> T run(final AScriptTaskR<T> scriptTask) {
        //get session
        final Rsession rsession;
        try {
            rsession = RsessionObjectPool.INSTANCE.borrowObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        try {
            //inputs
            final RserveScriptTaskInputsR inputs = new RserveScriptTaskInputsR(rsession);
            scriptTask.populateInputs(inputs);
            inputs.close();

            //execute
            eval(rsession, scriptTask.getScriptResourceAsString());

            //results
            final RserveScriptTaskResultsR results = new RserveScriptTaskResultsR(rsession);
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
    public RserveScriptTaskRunnerR getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RserveScriptTaskRunnerR.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
