package de.invesdwin.context.r.runtime.contract;

import java.io.Closeable;

public interface IScriptTaskResults extends Closeable {

    String getString(String variable);

    String[] getStringVector(String variable);

    String[][] getStringMatrix(String variable);

    Double getDouble(String variable);

    Double[] getDoubleVector(String variable);

    Double[][] getDoubleMatrix(String variable);

    @Override
    void close();

    Object getEngine();

}
