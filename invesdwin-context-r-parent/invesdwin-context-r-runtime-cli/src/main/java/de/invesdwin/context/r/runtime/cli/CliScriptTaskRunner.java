package de.invesdwin.context.r.runtime.cli;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.cli.pool.RCallerObjectPool;
import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.context.r.runtime.contract.ScriptTask;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class CliScriptTaskRunner implements IScriptTaskRunner, FactoryBean<CliScriptTaskRunner> {

    public static final CliScriptTaskRunner INSTANCE = new CliScriptTaskRunner();

    public static final String INTERNAL_RESULT_VARIABLE = CliScriptTaskRunner.class.getSimpleName() + "_result";

    private CliScriptTaskRunner() {}

    @Override
    public IScriptTaskResults run(final ScriptTask scriptTask) {
        //get session
        final RCaller rcaller;
        try {
            rcaller = RCallerObjectPool.INSTANCE.borrowObject();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        try {
            rcaller.getRCode().clearOnline();
            rcaller.getRCode().addRCode(scriptTask.getScriptResourceAsString());
            //provide access to all variables
            rcaller.getRCode().addRCode(INTERNAL_RESULT_VARIABLE + " <- ls()");
            rcaller.runAndReturnResultOnline(INTERNAL_RESULT_VARIABLE);
            return newResult(rcaller);
        } catch (final Throwable t) {
            try {
                RCallerObjectPool.INSTANCE.invalidateObject(rcaller);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            throw Throwables.propagate(t);
        }
    }

    private IScriptTaskResults newResult(final RCaller rcaller) {
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
                    RCallerObjectPool.INSTANCE.returnObject(rcaller);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
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
