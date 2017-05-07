package de.invesdwin.context.r.runtime.rserve;

import javax.annotation.concurrent.NotThreadSafe;

import org.math.R.Rsession;

import de.invesdwin.context.r.runtime.contract.IScriptTaskInputs;
import de.invesdwin.util.assertions.Assertions;

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
    public void putString(final String variable, final String value) {
        if (value == null) {
            putExpression(variable, "NA_character_");
        } else {
            rsession.set(variable, value);
        }
    }

    @Override
    public void putStringVector(final String variable, final String[] value) {
        rsession.set(variable, value);
    }

    @Override
    public void putStringMatrix(final String variable, final String[][] value) {
        final int rows = value.length;
        final int cols = value[0].length;
        final String[] flatMatrix = new String[rows * cols];
        int i = 0;
        for (int row = 0; row < rows; row++) {
            Assertions.checkEquals(value[row].length, cols);
            for (int col = 0; col < cols; col++) {
                flatMatrix[i] = value[row][col];
                i++;
            }
        }
        putStringVector(variable, flatMatrix);
        putExpression(variable, "matrix(" + variable + ", " + rows + ", " + cols + ", TRUE)");
    }

    @Override
    public void putDouble(final String variable, final double value) {
        rsession.set(variable, value);
    }

    @Override
    public void putDoubleVector(final String variable, final double[] value) {
        rsession.set(variable, value);
    }

    @Override
    public void putDoubleMatrix(final String variable, final double[][] value) {
        rsession.set(variable, value);
    }

    @Override
    public void putBoolean(final String variable, final boolean value) {
        final double doubleValue;
        if (value) {
            doubleValue = 1D;
        } else {
            doubleValue = 0D;
        }
        rsession.set(variable, doubleValue);
        putExpression(variable, "as.logical(" + variable + ")");
    }

    @Override
    public void putBooleanVector(final String variable, final boolean[] value) {
        final double[] doubleValue = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            if (value[i]) {
                doubleValue[i] = 1D;
            } else {
                doubleValue[i] = 0D;
            }
        }
        rsession.set(variable, doubleValue);
        putExpression(variable, "as.logical(" + variable + ")");
    }

    @Override
    public void putBooleanMatrix(final String variable, final boolean[][] value) {
        final double[][] doubleValue = new double[value.length][];
        for (int i = 0; i < value.length; i++) {
            final boolean[] vector = value[i];
            final double[] doubleVector = new double[vector.length];
            for (int j = 0; j < vector.length; j++) {
                if (vector[j]) {
                    doubleVector[j] = 1D;
                } else {
                    doubleVector[j] = 0D;
                }
            }
            doubleValue[i] = doubleVector;
        }
        rsession.set(variable, doubleValue);
        putExpression(variable, "array(as.logical(" + variable + "), dim(" + variable + "))");
    }

    @Override
    public void putExpression(final String variable, final String expression) {
        RserveScriptTaskRunner.eval(rsession, variable + " <- " + expression);
    }

}
