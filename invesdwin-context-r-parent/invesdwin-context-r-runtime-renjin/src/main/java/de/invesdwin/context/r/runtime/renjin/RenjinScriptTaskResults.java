package de.invesdwin.context.r.runtime.renjin;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.ScriptException;

import org.renjin.primitives.matrix.Matrix;
import org.renjin.script.RenjinScriptEngine;
import org.renjin.sexp.Logical;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.Vector;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.renjin.pool.RenjinScriptEngineObjectPool;

@NotThreadSafe
public class RenjinScriptTaskResults implements IScriptTaskResults {
    private final RenjinScriptEngine renjinScriptEngine;

    public RenjinScriptTaskResults(final RenjinScriptEngine renjinScriptEngine) {
        this.renjinScriptEngine = renjinScriptEngine;
    }

    @Override
    public void close() {
        try {
            RenjinScriptEngineObjectPool.INSTANCE.returnObject(renjinScriptEngine);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RenjinScriptEngine getEngine() {
        return renjinScriptEngine;
    }

    @Override
    public String getString(final String variable) {
        try {
            final Vector sexp = (Vector) renjinScriptEngine.eval(variable);
            if (sexp.length() == 0 || sexp.isElementNA(0)) {
                return null;
            } else {
                return sexp.asString();
            }
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getStringVector(final String variable) {
        try {
            final Vector sexp = (Vector) renjinScriptEngine.eval(variable);
            final String[] array = new String[sexp.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = sexp.getElementAsString(i);
            }
            return array;
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[][] getStringMatrix(final String variable) {
        try {
            final Matrix sexp = new Matrix((Vector) renjinScriptEngine.eval(variable));
            final String[][] matrix = new String[sexp.getNumRows()][];
            for (int row = 0; row < matrix.length; row++) {
                final String[] vector = new String[sexp.getNumCols()];
                for (int col = 0; col < vector.length; col++) {
                    vector[col] = sexp.getVector().getElementAsString(org.renjin.primitives.Indexes
                            .matrixIndexToVectorIndex(row, col, sexp.getNumRows(), sexp.getNumCols()));
                }
                matrix[row] = vector;
            }
            return matrix;
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getDouble(final String variable) {
        try {
            final SEXP sexp = (SEXP) renjinScriptEngine.eval(variable);
            return sexp.asReal();
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double[] getDoubleVector(final String variable) {
        try {
            final Vector sexp = (Vector) renjinScriptEngine.eval(variable);
            final double[] array = new double[sexp.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = sexp.getElementAsDouble(i);
            }
            return array;
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double[][] getDoubleMatrix(final String variable) {
        try {
            final Matrix sexp = new Matrix((Vector) renjinScriptEngine.eval(variable));
            final double[][] matrix = new double[sexp.getNumRows()][];
            for (int row = 0; row < matrix.length; row++) {
                final double[] vector = new double[sexp.getNumCols()];
                for (int col = 0; col < vector.length; col++) {
                    vector[col] = sexp.getElementAsDouble(row, col);
                }
                matrix[row] = vector;
            }
            return matrix;
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getBoolean(final String variable) {
        try {
            final SEXP sexp = (SEXP) renjinScriptEngine.eval(variable);
            return sexp.asLogical() == Logical.TRUE;
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean[] getBooleanVector(final String variable) {
        try {
            final Vector sexp = (Vector) renjinScriptEngine.eval(variable);
            final boolean[] array = new boolean[sexp.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = sexp.getElementAsLogical(i) == Logical.TRUE;
            }
            return array;
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean[][] getBooleanMatrix(final String variable) {
        try {
            final Matrix sexp = new Matrix((Vector) renjinScriptEngine.eval(variable));
            final boolean[][] matrix = new boolean[sexp.getNumRows()][];
            for (int row = 0; row < matrix.length; row++) {
                final boolean[] vector = new boolean[sexp.getNumCols()];
                for (int col = 0; col < vector.length; col++) {
                    vector[col] = sexp.getElementAsInt(row, col) > 0;
                }
                matrix[row] = vector;
            }
            return matrix;
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

}