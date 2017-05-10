package de.invesdwin.context.r.runtime.rcaller;

import javax.annotation.concurrent.NotThreadSafe;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.integration.script.IScriptTaskEngine;

@NotThreadSafe
public class RCallerScriptTaskEngineR implements IScriptTaskEngine {

    private RCaller rcaller;
    private final RCallerScriptTaskInputsR inputs;
    private final RCallerScriptTaskResultsR results;

    public RCallerScriptTaskEngineR(final RCaller rcaller) {
        this.rcaller = rcaller;
        this.inputs = new RCallerScriptTaskInputsR(this);
        this.results = new RCallerScriptTaskResultsR(this);
    }

    @Override
    public void eval(final String expression) {
        rcaller.getRCode().addRCode(expression);
        rcaller.getRCode().addRCode(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE + " <- c()");
        rcaller.runAndReturnResultOnline(RCallerScriptTaskRunnerR.INTERNAL_RESULT_VARIABLE);
    }

    @Override
    public RCallerScriptTaskInputsR getInputs() {
        return inputs;
    }

    @Override
    public RCallerScriptTaskResultsR getResults() {
        return results;
    }

    @Override
    public void close() {
        rcaller = null;
    }

    @Override
    public RCaller unwrap() {
        return rcaller;
    }

}
