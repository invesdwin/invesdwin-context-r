package de.invesdwin.context.r.runtime.renjin;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.ScriptException;

import org.apache.commons.lang3.ArrayUtils;
import org.renjin.primitives.matrix.DoubleMatrixBuilder;
import org.renjin.primitives.matrix.IntMatrixBuilder;
import org.renjin.primitives.matrix.StringMatrixBuilder;
import org.renjin.script.RenjinScriptEngine;

import de.invesdwin.context.r.runtime.contract.IScriptTaskInputs;

@NotThreadSafe
public class RenjinScriptTaskInputs implements IScriptTaskInputs {

    private final RenjinScriptEngine renjinScriptEngine;

    public RenjinScriptTaskInputs(final RenjinScriptEngine renjinScriptEngine) {
        this.renjinScriptEngine = renjinScriptEngine;
    }

    @Override
    public RenjinScriptEngine getEngine() {
        return renjinScriptEngine;
    }

    @Override
    public void putString(final String variable, final String value) {
        if (value == null) {
            putExpression(variable, "NA_character_");
        } else {
            renjinScriptEngine.put(variable, value);
        }
    }

    @Override
    public void putStringVector(final String variable, final String[] value) {
        renjinScriptEngine.put(variable, value);
    }

    @Override
    public void putStringMatrix(final String variable, final String[][] value) {
        final int rows = value.length;
        final int cols = value[0].length;
        final StringMatrixBuilder matrix = new StringMatrixBuilder(rows, cols);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix.setValue(row, col, value[row][col]);
            }
        }
        renjinScriptEngine.put(variable, matrix.build());
    }

    @Override
    public void putDouble(final String variable, final double value) {
        renjinScriptEngine.put(variable, value);
    }

    @Override
    public void putDoubleVector(final String variable, final double[] value) {
        renjinScriptEngine.put(variable, value);
    }

    @Override
    public void putDoubleMatrix(final String variable, final double[][] value) {
        final int rows = value.length;
        final int cols = value[0].length;
        final DoubleMatrixBuilder matrix = new DoubleMatrixBuilder(rows, cols);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix.setValue(row, col, value[row][col]);
            }
        }
        renjinScriptEngine.put(variable, matrix.build());
    }

    @Override
    public void putBoolean(final String variable, final boolean value) {
        renjinScriptEngine.put(variable, value);
    }

    @Override
    public void putBooleanVector(final String variable, final boolean[] value) {
        renjinScriptEngine.put(variable, ArrayUtils.toObject(value));
    }

    @Override
    public void putBooleanMatrix(final String variable, final boolean[][] value) {
        final int rows = value.length;
        final int cols = value[0].length;
        final IntMatrixBuilder matrix = new IntMatrixBuilder(rows, cols);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                final int intValue;
                if (value[row][col]) {
                    intValue = 1;
                } else {
                    intValue = 0;
                }
                matrix.setValue(row, col, intValue);
            }
        }
        renjinScriptEngine.put(variable, matrix.build());
        putExpression(variable, "array(as.logical(" + variable + "), dim(" + variable + "))");
    }

    @Override
    public void putExpression(final String variable, final String expression) {
        try {
            renjinScriptEngine.eval(variable + " <- " + expression);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

}
