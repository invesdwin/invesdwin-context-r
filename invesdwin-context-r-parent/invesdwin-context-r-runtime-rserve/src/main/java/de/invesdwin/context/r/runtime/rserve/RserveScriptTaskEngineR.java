package de.invesdwin.context.r.runtime.rserve;

import javax.annotation.concurrent.NotThreadSafe;

import org.math.R.Rsession;
import org.rosuda.REngine.REXP;

import de.invesdwin.context.integration.script.IScriptTaskEngine;

@NotThreadSafe
public class RserveScriptTaskEngineR implements IScriptTaskEngine {

    private Rsession rsession;
    private final RserveScriptTaskInputsR inputs;
    private final RserveScriptTaskResultsR results;

    public RserveScriptTaskEngineR(final Rsession rsession) {
        this.rsession = rsession;
        this.inputs = new RserveScriptTaskInputsR(this);
        this.results = new RserveScriptTaskResultsR(this);
    }

    @Override
    public void eval(final String expression) {
        final REXP eval = rsession.eval(expression);
        if (eval == null) {
            throw new IllegalStateException(
                    String.valueOf(de.invesdwin.context.r.runtime.rserve.pool.internal.RsessionLogger.get(rsession)
                            .getErrorMessage()));
        }
    }

    @Override
    public RserveScriptTaskInputsR getInputs() {
        return inputs;
    }

    @Override
    public RserveScriptTaskResultsR getResults() {
        return results;
    }

    @Override
    public void close() {
        rsession = null;
    }

    @Override
    public Rsession unwrap() {
        return rsession;
    }

}
