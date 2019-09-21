package de.invesdwin.context.r.runtime.renjin.pool.internal;

import java.io.PrintWriter;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;
import javax.script.ScriptException;

import org.renjin.eval.SessionBuilder;
import org.renjin.script.RenjinScriptEngine;
import org.renjin.script.RenjinScriptEngineFactory;
import org.springframework.beans.factory.FactoryBean;
import org.zeroturnaround.exec.stream.slf4j.Slf4jDebugOutputStream;
import org.zeroturnaround.exec.stream.slf4j.Slf4jWarnOutputStream;

import de.invesdwin.context.pool.IPoolableObjectFactory;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;

@ThreadSafe
@Named
public final class RenjinScriptEnginePoolableObjectFactory
        implements IPoolableObjectFactory<RenjinScriptEngine>, FactoryBean<RenjinScriptEnginePoolableObjectFactory> {

    public static final RenjinScriptEnginePoolableObjectFactory INSTANCE = new RenjinScriptEnginePoolableObjectFactory();
    private static final RenjinScriptEngineFactory FACTORY = new RenjinScriptEngineFactory();

    private RenjinScriptEnginePoolableObjectFactory() {}

    @Override
    public RenjinScriptEngine makeObject() {
        final RenjinScriptEngine engine = FACTORY.getScriptEngine(
                new SessionBuilder().withDefaultPackages().setPackageLoader(ExtendedPackageLoader.INSTANCE).build());
        engine.getContext().setWriter(new PrintWriter(new Slf4jDebugOutputStream(IScriptTaskRunnerR.LOG)));
        engine.getContext().setErrorWriter(new PrintWriter(new Slf4jWarnOutputStream(IScriptTaskRunnerR.LOG)));
        return engine;
    }

    @Override
    public void destroyObject(final RenjinScriptEngine obj) {
        obj.getSession().close();
    }

    @Override
    public boolean validateObject(final RenjinScriptEngine obj) {
        return true;
    }

    @Override
    public void activateObject(final RenjinScriptEngine obj) {}

    @Override
    public void passivateObject(final RenjinScriptEngine obj) {
        try {
            obj.eval(IScriptTaskRunnerR.CLEANUP_SCRIPT);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RenjinScriptEnginePoolableObjectFactory getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RenjinScriptEnginePoolableObjectFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
