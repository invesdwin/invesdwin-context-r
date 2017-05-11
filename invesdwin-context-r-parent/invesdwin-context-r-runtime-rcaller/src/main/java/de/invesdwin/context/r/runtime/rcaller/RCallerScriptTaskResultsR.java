package de.invesdwin.context.r.runtime.rcaller;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResultsR;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class RCallerScriptTaskResultsR implements IScriptTaskResultsR {
    private final RCallerScriptTaskEngineR engine;

    public RCallerScriptTaskResultsR(final RCallerScriptTaskEngineR engine) {
        this.engine = engine;
    }

    @Override
    public RCallerScriptTaskEngineR getEngine() {
        return engine;
    }

    private void requestVariable(final String variable) {
        engine.unwrap().runAndReturnResultOnline(variable);
    }

    @Override
    public String getString(final String variable) {
        requestVariable(variable);
        final String[] array = replaceNaWithNull(engine.unwrap().getParser().getAsStringArray(variable));
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
        return replaceNaWithNull(engine.unwrap().getParser().getAsStringArray(variable));
    }

    @Override
    public String[][] getStringMatrix(final String variable) {
        final String[] ct = getStringVector(variable);
        if (ct == null) {
            return null;
        }
        engine.unwrap()
                .getRCode()
                .addRCode(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE + " <- dim(" + variable + ")");
        final int[] ds = getIntegerVector(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE);
        if ((ds == null) || (ds.length != 2)) {
            return null;
        }
        final int m = ds[0];
        final int n = ds[1];
        final String[][] r = new String[m][n];

        int i = 0;
        int k = 0;
        while (i < n) {
            int j = 0;
            while (j < m) {
                r[(j++)][i] = ct[(k++)];
            }
            i++;
        }
        return r;
    }

    @Override
    public double getDouble(final String variable) {
        requestVariable(variable);
        final double[] array = engine.unwrap().getParser().getAsDoubleArray(variable);
        Assertions.checkEquals(array.length, 1);
        return array[0];
    }

    @Override
    public double[] getDoubleVector(final String variable) {
        requestVariable(variable);
        return engine.unwrap().getParser().getAsDoubleArray(variable);
    }

    @Override
    public double[][] getDoubleMatrix(final String variable) {
        //not using engine getDoubleMatrix since it transposes the matrix as a side effect...
        final double[] ct = getDoubleVector(variable);
        if (ct == null) {
            return null;
        }
        final int[] ds = engine.unwrap().getParser().getDimensions(variable);
        if ((ds == null) || (ds.length != 2)) {
            return null;
        }
        final int m = ds[0];
        final int n = ds[1];
        final double[][] r = new double[m][n];

        int i = 0;
        int k = 0;
        while (i < n) {
            int j = 0;
            while (j < m) {
                r[(j++)][i] = ct[(k++)];
            }
            i++;
        }
        return r;
    }

    @Override
    public int getInteger(final String variable) {
        requestVariable(variable);
        final int[] array = engine.unwrap().getParser().getAsIntArray(variable);
        Assertions.checkEquals(array.length, 1);
        return array[0];
    }

    @Override
    public int[] getIntegerVector(final String variable) {
        requestVariable(variable);
        return engine.unwrap().getParser().getAsIntArray(variable);
    }

    @Override
    public int[][] getIntegerMatrix(final String variable) {
        final int[] ct = getIntegerVector(variable);
        if (ct == null) {
            return null;
        }
        final int[] ds = engine.unwrap().getParser().getDimensions(variable);
        if ((ds == null) || (ds.length != 2)) {
            return null;
        }
        final int m = ds[0];
        final int n = ds[1];
        final int[][] r = new int[m][n];

        int i = 0;
        int k = 0;
        while (i < n) {
            int j = 0;
            while (j < m) {
                r[(j++)][i] = ct[(k++)];
            }
            i++;
        }
        return r;
    }

    @Override
    public boolean getBoolean(final String variable) {
        requestVariable(variable);
        final boolean[] array = engine.unwrap().getParser().getAsLogicalArray(variable);
        Assertions.checkEquals(array.length, 1);
        return array[0];
    }

    @Override
    public boolean[] getBooleanVector(final String variable) {
        requestVariable(variable);
        return engine.unwrap().getParser().getAsLogicalArray(variable);
    }

    @Override
    public boolean[][] getBooleanMatrix(final String variable) {
        engine.unwrap().getRCode().addRCode(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE + " <- array(as.numeric("
                + variable + "), dim(" + variable + "))");
        final double[][] matrix = getDoubleMatrix(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE);
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