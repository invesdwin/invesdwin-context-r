package de.invesdwin.context.r.runtime.rserve;

import javax.annotation.concurrent.NotThreadSafe;

import org.math.R.Rsession;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.rserve.pool.RsessionObjectPool;
import de.invesdwin.util.assertions.Assertions;

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
        try {
            final REXP rexp = rsession.eval(variable);
            final boolean[] na = rexp.isNA();
            Assertions.checkEquals(na.length, 1);
            if (na[0]) {
                return null;
            } else {
                return rexp.asString();
            }
        } catch (final REXPMismatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getStringVector(final String variable) {
        try {
            return rsession.eval(variable).asStrings();
        } catch (final REXPMismatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double[] getDoubleVector(final String variable) {
        try {
            return rsession.eval(variable).asDoubles();
        } catch (final REXPMismatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double[][] getDoubleMatrix(final String variable) {
        try {
            return rsession.eval(variable).asDoubleMatrix();
        } catch (final REXPMismatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getDouble(final String variable) {
        try {
            return rsession.eval(variable).asDouble();
        } catch (final REXPMismatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getBoolean(final String variable) {
        try {
            return rsession.eval(variable).asInteger() > 0;
        } catch (final REXPMismatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean[] getBooleanVector(final String variable) {
        try {
            final int[] ints = rsession.eval(variable).asIntegers();
            final boolean[] booleanVector = new boolean[ints.length];
            for (int i = 0; i < ints.length; i++) {
                booleanVector[i] = ints[i] > 0;
            }
            return booleanVector;
        } catch (final REXPMismatchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean[][] getBooleanMatrix(final String variable) {
        try {
            final double[][] matrix = rsession.eval(variable).asDoubleMatrix();
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
        } catch (final REXPMismatchException e) {
            throw new RuntimeException(e);
        }
    }

}