package de.invesdwin.context.r.runtime.contract;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IScriptTaskResults extends Closeable {

    String getString(String variable);

    String[] getStringVector(String variable);

    default List<String> getStringVectorAsList(final String variable) {
        return Arrays.asList(getStringVector(variable));
    }

    String[][] getStringMatrix(String variable);

    default List<List<String>> getStringMatrixAsList(final String variable) {
        final String[][] matrix = getStringMatrix(variable);
        final List<List<String>> matrixAsList = new ArrayList<>(matrix.length);
        for (final String[] vector : matrix) {
            matrixAsList.add(Arrays.asList(vector));
        }
        return matrixAsList;
    }

    Double getDouble(String variable);

    Double[] getDoubleVector(String variable);

    default List<Double> getDoubleVectorAsList(final String variable) {
        return Arrays.asList(getDoubleVector(variable));
    }

    Double[][] getDoubleMatrix(String variable);

    default List<List<Double>> getDoubleMatrixAsList(final String variable) {
        final Double[][] matrix = getDoubleMatrix(variable);
        final List<List<Double>> matrixAsList = new ArrayList<>(matrix.length);
        for (final Double[] vector : matrix) {
            matrixAsList.add(Arrays.asList(vector));
        }
        return matrixAsList;
    }

    @Override
    void close();

    Object getEngine();

}
