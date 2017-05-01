package de.invesdwin.context.r.runtime.contract;

public interface IScriptResultExpression<T> {

    /**
     * The R expression with which the result can be extracted from the actual script. Can be the variable name or
     * something more complex.
     */
    String getExpression();

    /**
     * The result of the expression as extracted from the script result map.
     */
    T getResult(IScriptResults resultMap);

}
