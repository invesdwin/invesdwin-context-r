package de.invesdwin.context.r.runtime.contract;

import java.util.List;

public interface IScriptTaskInputs {

    void putString(String variable, String value);

    void putStringVector(String variable, String[] value);

    void putStringVector(String variable, List<String> value);

    void putStringMatrix(String variable, String[][] value);

    void putStringMatrix(String variable, List<? extends List<String>> value);

    void putDouble(String variable, Double value);

    void putDoubleVector(String variable, Double[] value);

    void putDoubleVector(String variable, List<Double> value);

    void putDoubleMatrix(String variable, Double[][] value);

    void putDoubleMatrix(String variable, List<? extends List<Double>> value);

    Object getEngine();

}
