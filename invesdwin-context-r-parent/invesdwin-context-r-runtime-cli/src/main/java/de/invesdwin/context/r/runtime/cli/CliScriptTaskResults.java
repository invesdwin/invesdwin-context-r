package de.invesdwin.context.r.runtime.cli;

import javax.annotation.concurrent.NotThreadSafe;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.cli.pool.RCallerObjectPool;
import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;

@NotThreadSafe
public class CliScriptTaskResults implements IScriptTaskResults {
    private final RCaller rcaller;

    public CliScriptTaskResults(final RCaller rcaller) {
        this.rcaller = rcaller;
    }

    @Override
    public void close() {
        try {
            RCallerObjectPool.INSTANCE.returnObject(rcaller);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RCaller getEngine() {
        return rcaller;
    }

    @Override
    public String getString(final String variable) {
        return null;
    }

    @Override
    public String[] getStringVector(final String variable) {
        return null;
    }

    @Override
    public String[][] getStringMatrix(final String variable) {
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
    public Boolean getBoolean(final String variable) {
        return null;
    }

    @Override
    public Boolean[] getBooleanVector(final String variable) {
        return null;
    }

    @Override
    public Boolean[][] getBooleanMatrix(final String variable) {
        return null;
    }

}