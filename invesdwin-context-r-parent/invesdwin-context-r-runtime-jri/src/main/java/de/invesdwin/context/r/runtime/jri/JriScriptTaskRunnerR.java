package de.invesdwin.context.r.runtime.jri;

import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.rosuda.JRI.Rengine;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.AScriptTaskR;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.jri.internal.LoggingRMainLoopCallbacks;
import de.invesdwin.instrument.DynamicInstrumentationReflections;
import de.invesdwin.util.concurrent.Locks;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class JriScriptTaskRunnerR implements IScriptTaskRunnerR, FactoryBean<JriScriptTaskRunnerR> {

    public static final JriScriptTaskRunnerR INSTANCE = new JriScriptTaskRunnerR();

    @GuardedBy("RENGINE_LOCK")
    private static final Rengine RENGINE;
    private static final ReentrantLock RENGINE_LOCK;

    static {
        DynamicInstrumentationReflections.addPathToJavaLibraryPath(JriProperties.JRI_LIBRARY_PATH);
        if (Rengine.getMainEngine() != null) {
            RENGINE = Rengine.getMainEngine();
        } else {
            RENGINE = new Rengine(new String[] { "--vanilla" }, false, null);
        }
        if (!RENGINE.waitForR()) {
            throw new IllegalStateException("Cannot load R");
        }
        RENGINE.addMainLoopCallbacks(LoggingRMainLoopCallbacks.INSTANCE);
        RENGINE_LOCK = Locks.newReentrantLock(JriScriptTaskRunnerR.class.getSimpleName() + "_RENGINE_LOCK");
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
            final JriScriptTaskEngineR engine = new JriScriptTaskEngineR(RENGINE);
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

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
