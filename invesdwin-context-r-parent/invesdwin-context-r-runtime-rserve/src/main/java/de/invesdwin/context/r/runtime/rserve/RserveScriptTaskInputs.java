package de.invesdwin.context.r.runtime.rserve;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.math.R.Rsession;

import de.invesdwin.context.r.runtime.contract.IScriptTaskInputs;

@NotThreadSafe
public class RserveScriptTaskInputs implements IScriptTaskInputs {

    private final Rsession rsession;

    public RserveScriptTaskInputs(final Rsession rsession) {
        this.rsession = rsession;
    }

    @Override
    public Rsession getEngine() {
        return rsession;
    }

    @Override
    public void putString(final String variable, final String value) {}

    @Override
    public void putStringVector(final String variable, final String[] value) {}

    @Override
    public void putStringVectorAsList(final String variable, final List<String> value) {}

    @Override
    public void putStringMatrix(final String variable, final String[][] value) {}

    @Override
    public void putStringMatrixAsList(final String variable, final List<? extends List<String>> value) {}

    @Override
    public void putDouble(final String variable, final Double value) {}

    @Override
    public void putDoubleVector(final String variable, final Double[] value) {}

    @Override
    public void putDoubleVectorAsList(final String variable, final List<Double> value) {}

    @Override
    public void putDoubleMatrix(final String variable, final Double[][] value) {}

    @Override
    public void putDoubleMatrixAsList(final String variable, final List<? extends List<Double>> value) {}

    @Override
    public void putBoolean(final String variable, final Boolean value) {}

    @Override
    public void putBooleanVector(final String variable, final Boolean[] value) {}

    @Override
    public void putBooleanVectorAsList(final String variable, final List<Boolean> value) {}

    @Override
    public void putBooleanMatrix(final String variable, final Boolean[][] value) {}

    @Override
    public void putBooleanMatrixAsList(final String variable, final List<? extends List<Boolean>> value) {}

}
