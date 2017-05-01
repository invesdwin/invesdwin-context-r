package de.invesdwin.context.r.runtime.contract;

public interface IScriptResultMap {

    String getString(IScriptResult<?> result);

    Double getDouble(IScriptResult<?> result);

    Double[] getDoubleVector(IScriptResult<?> result);

    Double[][] getDoubleMatrix(IScriptResult<?> result);

}
