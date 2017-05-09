package de.invesdwin.context.r.runtime.jri;

import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.AScriptTaskR;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.jri.internal.LoggingRMainLoopCallbacks;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class JriScriptTaskRunnerR implements IScriptTaskRunnerR, FactoryBean<JriScriptTaskRunnerR> {

    public static final JriScriptTaskRunnerR INSTANCE = new JriScriptTaskRunnerR();

    @GuardedBy("RENGINE_LOCK")
    private static final Rengine RENGINE;
    private static final ReentrantLock RENGINE_LOCK;

    static {
        if (Rengine.getMainEngine() != null) {
            RENGINE = Rengine.getMainEngine();
        } else {
            RENGINE = new Rengine(new String[] { "--vanilla" }, false, null);
        }
        if (!RENGINE.waitForR()) {
            throw new IllegalStateException("Cannot load R");
        }
        RENGINE.addMainLoopCallbacks(LoggingRMainLoopCallbacks.INSTANCE);
        RENGINE_LOCK = new ReentrantLock();
    }

    /**
     * public for ServiceLoader support
     */
    public JriScriptTaskRunnerR() {}

    @Override
    public <T> T run(final AScriptTaskR<T> scriptTask) {
        //get session
        RENGINE_LOCK.lock();
        try {
            //inputs
            final JriScriptTaskInputsR inputs = new JriScriptTaskInputsR(RENGINE);
            scriptTask.populateInputs(inputs);
            inputs.close();

            //execute
            eval(RENGINE, scriptTask.getScriptResourceAsString());

            //results
            final JriScriptTaskResultsR results = new JriScriptTaskResultsR(RENGINE);
            final T result = scriptTask.extractResults(results);
            results.close();

            //return
            RENGINE_LOCK.unlock();
            return result;
        } catch (final Throwable t) {
            RENGINE_LOCK.unlock();
            throw Throwables.propagate(t);
        } finally {
            LoggingRMainLoopCallbacks.INSTANCE.reset();
        }
    }

    public static void eval(final Rengine rengine, final String expression) {
        final REXP eval = RENGINE.eval("eval(parse(text=\"" + expression.replace("\"", "\\\"") + "\"))");
        if (eval == null) {
            throw new IllegalStateException(String.valueOf(LoggingRMainLoopCallbacks.INSTANCE.getErrorMessage()));
        }
    }

    @Override
    public JriScriptTaskRunnerR getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return JriScriptTaskRunnerR.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
