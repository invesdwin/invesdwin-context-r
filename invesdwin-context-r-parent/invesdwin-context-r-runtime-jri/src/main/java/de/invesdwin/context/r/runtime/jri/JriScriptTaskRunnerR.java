package de.invesdwin.context.r.runtime.jri;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.r.runtime.contract.AScriptTaskR;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.jri.internal.LoggingRMainLoopCallbacks;
import de.invesdwin.context.r.runtime.jri.internal.RengineWrapper;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class JriScriptTaskRunnerR implements IScriptTaskRunnerR, FactoryBean<JriScriptTaskRunnerR> {

    public static final JriScriptTaskRunnerR INSTANCE = new JriScriptTaskRunnerR();

    /**
     * public for ServiceLoader support
     */
    public JriScriptTaskRunnerR() {
    }

    @Override
    public <T> T run(final AScriptTaskR<T> scriptTask) {
        //get session
        final JriScriptTaskEngineR engine = new JriScriptTaskEngineR(RengineWrapper.INSTANCE);
        engine.getSharedLock().lock();
        try {
            //inputs
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            engine.getSharedLock().unlock();
            return result;
        } catch (final Throwable t) {
            engine.getSharedLock().unlock();
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
