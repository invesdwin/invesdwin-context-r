package de.invesdwin.context.r.runtime.rserve;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.context.r.runtime.contract.ScriptTask;
import de.invesdwin.context.r.runtime.rserve.pool.RsessionObjectPool;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class RserveScriptTaskRunner implements IScriptTaskRunner, FactoryBean<RserveScriptTaskRunner> {

    public static final RserveScriptTaskRunner INSTANCE = new RserveScriptTaskRunner();

    private RserveScriptTaskRunner() {}

    @Override
    public IScriptTaskResults run(final ScriptTask scriptTask) {
        //get session
        final Rsession rsession;
        try {
            rsession = RsessionObjectPool.INSTANCE.borrowObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        //eval
        final REXP eval;
        try {
            eval = rsession.eval(scriptTask.getScriptResourceAsString());
        } catch (final Throwable t) {
            try {
                RsessionObjectPool.INSTANCE.invalidateObject(rsession);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            throw Throwables.propagate(t);
        }
        //handle error or return result
        try {
            if (eval == null) {
                throw new IllegalStateException(
                        String.valueOf(de.invesdwin.context.r.runtime.rserve.pool.internal.RsessionLogger.get(rsession)
                                .getErrorMessage()));
            }
            return newResult(rsession);
        } catch (final Throwable t) {
            try {
                RsessionObjectPool.INSTANCE.returnObject(rsession);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            throw Throwables.propagate(t);
        }
    }

    private IScriptTaskResults newResult(final Rsession rsession) {
        return new IScriptTaskResults() {

            @Override
            public String getString(final String variable) {
                return null;
            }

            @Override
            public Double[] getDoubleVector(final String variable) {
                return null;
            }

            @Override
            public Double[][] getDoubleMatrix(final String variable) {
                return null;
            }

            @Override
            public Double getDouble(final String variable) {
                return null;
            }

            @Override
            public void close() {
                try {
                    RsessionObjectPool.INSTANCE.returnObject(rsession);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
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
