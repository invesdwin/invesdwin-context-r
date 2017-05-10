package de.invesdwin.context.r.runtime.renjin;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.ScriptException;

import org.renjin.script.RenjinScriptEngine;

import de.invesdwin.context.integration.script.IScriptTaskEngine;

@NotThreadSafe
public class RenjinScriptTaskEngineR implements IScriptTaskEngine {

    private RenjinScriptEngine renjinScriptEngine;
    private final RenjinScriptTaskInputsR inputs;
    private final RenjinScriptTaskResultsR results;

    public RenjinScriptTaskEngineR(final RenjinScriptEngine renjinScriptEngine) {
        this.renjinScriptEngine = renjinScriptEngine;
        this.inputs = new RenjinScriptTaskInputsR(this);
        this.results = new RenjinScriptTaskResultsR(this);
    }

    @Override
    public void eval(final String expression) {
        try {
            renjinScriptEngine.eval(expression);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RenjinScriptTaskInputsR getInputs() {
        return inputs;
    }

    @Override
    public RenjinScriptTaskResultsR getResults() {
        return results;
    }

    @Override
    public void close() {
        renjinScriptEngine = null;
    }

    @Override
    public RenjinScriptEngine unwrap() {
        return renjinScriptEngine;
    }

}
