package de.invesdwin.context.r.runtime.rserve;

import javax.annotation.concurrent.NotThreadSafe;

import org.math.R.Rsession;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.rserve.pool.RsessionObjectPool;

@NotThreadSafe
public class RserveScriptTaskResults implements IScriptTaskResults {

    private final Rsession rsession;

    public RserveScriptTaskResults(final Rsession rsession) {
        this.rsession = rsession;
    }

    @Override
    public void close() {
        try {
            RsessionObjectPool.INSTANCE.returnObject(rsession);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Rsession getEngine() {
        return rsession;
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