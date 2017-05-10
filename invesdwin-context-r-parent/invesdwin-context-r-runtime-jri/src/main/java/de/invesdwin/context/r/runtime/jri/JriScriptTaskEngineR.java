package de.invesdwin.context.r.runtime.jri;

import javax.annotation.concurrent.NotThreadSafe;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.jri.internal.LoggingRMainLoopCallbacks;

@NotThreadSafe
public class JriScriptTaskEngineR implements IScriptTaskEngine {

    private Rengine rengine;
    private final JriScriptTaskInputsR inputs;
    private final JriScriptTaskResultsR results;

    public JriScriptTaskEngineR(final Rengine rengine) {
        this.rengine = rengine;
        this.inputs = new JriScriptTaskInputsR(this);
        this.results = new JriScriptTaskResultsR(this);
    }

    @Override
    public void eval(final String expression) {
        final REXP eval = rengine.eval("eval(parse(text=\"" + expression.replace("\"", "\\\"") + "\"))");
        if (eval == null) {
            throw new IllegalStateException(String.valueOf(LoggingRMainLoopCallbacks.INSTANCE.getErrorMessage()));
        }
    }

    @Override
    public JriScriptTaskInputsR getInputs() {
        return inputs;
    }

    @Override
    public JriScriptTaskResultsR getResults() {
        return results;
    }

    @Override
    public void close() {
        eval(IScriptTaskRunnerR.CLEANUP_SCRIPT);
        rengine = null;
    }

    @Override
    public Rengine unwrap() {
        return rengine;
    }

}
