package de.invesdwin.context.r.runtime.contract;

public interface IScriptResults {

    String getString(IScriptResultExpression<?> result);

    Double getDouble(IScriptResultExpression<?> result);

    Double[] getDoubleVector(IScriptResultExpression<?> result);

    Double[][] getDoubleMatrix(IScriptResultExpression<?> result);

}
