package de.invesdwin.context.r.runtime.jri;

import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.AScriptTask;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.context.r.runtime.jri.internal.LoggingRMainLoopCallbacks;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class JriScriptTaskRunner implements IScriptTaskRunner, FactoryBean<JriScriptTaskRunner> {

    public static final JriScriptTaskRunner INSTANCE = new JriScriptTaskRunner();

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

    private JriScriptTaskRunner() {}

    @Override
    public JriScriptTaskResults run(final AScriptTask scriptTask) {
        RENGINE_LOCK.lock();
        try {
            scriptTask.populateInputs(new JriScriptTaskInputs(RENGINE));
            eval(RENGINE, scriptTask.getScriptResourceAsString());
            return new JriScriptTaskResults(RENGINE, RENGINE_LOCK);
        } catch (final Throwable t) {
            unlockRengine();
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

    private void unlockRengine() {
        RENGINE_LOCK.unlock();
    }

    @Override
    public JriScriptTaskRunner getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return JriScriptTaskRunner.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
