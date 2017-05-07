package de.invesdwin.context.r.runtime.contract;

import java.util.List;

public interface IScriptTaskInputs {

    void putString(String variable, String value);

    void putStringVector(String variable, String[] value);

    void putStringVectorAsList(String variable, List<String> value);

    void putStringMatrix(String variable, String[][] value);

    void putStringMatrixAsList(String variable, List<? extends List<String>> value);

    void putDouble(String variable, Double value);

    void putDoubleVector(String variable, Double[] value);

    void putDoubleVectorAsList(String variable, List<Double> value);

    void putDoubleMatrix(String variable, Double[][] value);

    void putDoubleMatrixAsList(String variable, List<? extends List<Double>> value);

    void putBoolean(String variable, Boolean value);

    void putBooleanVector(String variable, Boolean[] value);

    void putBooleanVectorAsList(String variable, List<Boolean> value);

    void putBooleanMatrix(String variable, Boolean[][] value);

    void putBooleanMatrixAsList(String variable, List<? extends List<Boolean>> value);

    Object getEngine();

}
