package de.invesdwin.context.r.runtime.contract;

import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.util.math.Doubles;

public interface IScriptTaskInputsR extends IScriptTaskInputs {

    @Override
    default void putFloat(final String variable, final float value) {
        putDouble(variable, value);
    }

    @Override
    default void putFloatVector(final String variable, final float[] value) {
        final double[] doubleValue = Doubles.checkedCastVector(value);
        putDoubleVector(variable, doubleValue);
    }

    @Override
    default void putFloatMatrix(final String variable, final float[][] value) {
        final double[][] doubleValue = Doubles.checkedCastMatrix(value);
        putDoubleMatrix(variable, doubleValue);
    }

    @Override
    default void putLong(final String variable, final long value) {
        putDouble(variable, value);
    }

    @Override
    default void putLongVector(final String variable, final long[] value) {
        final double[] doubleValue = Doubles.checkedCastVector(value);
        putDoubleVector(variable, doubleValue);
    }

    @Override
    default void putLongMatrix(final String variable, final long[][] value) {
        final double[][] doubleValue = Doubles.checkedCastMatrix(value);
        putDoubleMatrix(variable, doubleValue);
    }

    @Override
    default void putExpression(final String variable, final String expression) {
        getEngine().eval(variable + " <- " + expression);
    }

}
