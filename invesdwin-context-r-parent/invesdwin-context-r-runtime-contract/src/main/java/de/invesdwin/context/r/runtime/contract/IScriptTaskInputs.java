package de.invesdwin.context.r.runtime.contract;

public interface IScriptTaskInputs {

    void putString(String variable, String value);

    void putStringVector(String variable, String[] value);

    void putStringMatrix(String variable, String[][] value);

    void putDouble(String variable, Double value);

    void putDoubleVector(String variable, Double[] value);

    void putDoubleMatrix(String variable, Double[][] value);

    Object getEngine();

}
