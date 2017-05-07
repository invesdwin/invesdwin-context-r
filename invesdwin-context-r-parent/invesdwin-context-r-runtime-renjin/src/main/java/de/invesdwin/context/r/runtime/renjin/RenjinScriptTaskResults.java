package de.invesdwin.context.r.runtime.renjin;

import javax.annotation.concurrent.NotThreadSafe;

import org.renjin.script.RenjinScriptEngine;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.renjin.pool.RenjinScriptEngineObjectPool;

@NotThreadSafe
public class RenjinScriptTaskResults implements IScriptTaskResults {
    private final RenjinScriptEngine renjinScriptEngine;

    public RenjinScriptTaskResults(final RenjinScriptEngine renjinScriptEngine) {
        this.renjinScriptEngine = renjinScriptEngine;
    }

    @Override
    public String getString(final String variable) {
        return null;
    }

    @Override
    public String[] getStringVector(final String variable) {
        return null;
    }

    @Override
    public String[][] getStringMatrix(final String variable) {
        return null;
    }

    @Override
    public Double[] getDoubleVector(final String variable) {
        return null;
    }

    @Override
    public Double[][] getDoubleMatrix(final String variable) {
        return null;
    }

    @Override
    public Double getDouble(final String variable) {
        return null;
    }

    @Override
    public void close() {
        try {
            RenjinScriptEngineObjectPool.INSTANCE.returnObject(renjinScriptEngine);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RenjinScriptEngine getEngine() {
        return renjinScriptEngine;
    }
}