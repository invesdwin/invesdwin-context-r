package de.invesdwin.context.r.runtime.cli;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.linear.MatrixUtils;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.cli.pool.RCallerObjectPool;
import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.util.assertions.Assertions;

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

    private void requestVariable(final String variable) {
        rcaller.getRCode().addRCode(variable);
        rcaller.runAndReturnResultOnline(variable);
    }

    @Override
    public String getString(final String variable) {
        requestVariable(variable);
        final String[] array = replaceNaWithNull(rcaller.getParser().getAsStringArray(variable));
        Assertions.checkEquals(array.length, 1);
        return array[0];
    }

    private String[] replaceNaWithNull(final String[] array) {
        for (int i = 0; i < array.length; i++) {
            if ("NA".equals(array[i])) {
                array[i] = null;
            }
        }
        return array;
    }

    @Override
    public String[] getStringVector(final String variable) {
        requestVariable(variable);
        return replaceNaWithNull(rcaller.getParser().getAsStringArray(variable));
    }

    @Override
    public double[] getDoubleVector(final String variable) {
        requestVariable(variable);
        return rcaller.getParser().getAsDoubleArray(variable);
    }

    @Override
    public double[][] getDoubleMatrix(final String variable) {
        requestVariable(variable);
        //the internal logic of RCaller is wrong here as the results are returned transposed... we have to undo that
        final int[] dims = rcaller.getParser().getDimensions(variable);
        final int rows = dims[0];
        final int cols = dims[1];
        final double[][] matrix = rcaller.getParser().getAsDoubleMatrix(variable, cols, rows);
        final double[][] transposedMatrix = MatrixUtils.createRealMatrix(matrix).transpose().getData();
        return transposedMatrix;
    }

    @Override
    public double getDouble(final String variable) {
        requestVariable(variable);
        final double[] array = rcaller.getParser().getAsDoubleArray(variable);
        Assertions.checkEquals(array.length, 1);
        return array[0];
    }

    @Override
    public boolean getBoolean(final String variable) {
        requestVariable(variable);
        final boolean[] array = rcaller.getParser().getAsLogicalArray(variable);
        Assertions.checkEquals(array.length, 1);
        return array[0];
    }

    @Override
    public boolean[] getBooleanVector(final String variable) {
        requestVariable(variable);
        return rcaller.getParser().getAsLogicalArray(variable);
    }

    @Override
    public boolean[][] getBooleanMatrix(final String variable) {
        rcaller.getRCode().addRCode(CliScriptTaskRunner.INTERNAL_RESULT_VARIABLE + " <- array(as.numeric(" + variable
                + "), dim(" + variable + "))");
        final double[][] matrix = getDoubleMatrix(CliScriptTaskRunner.INTERNAL_RESULT_VARIABLE);
        final boolean[][] booleanMatrix = new boolean[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            final double[] vector = matrix[i];
            final boolean[] booleanVector = new boolean[vector.length];
            for (int j = 0; j < vector.length; j++) {
                booleanVector[j] = vector[j] > 0;
            }
            booleanMatrix[i] = booleanVector;
        }
        return booleanMatrix;
    }

}