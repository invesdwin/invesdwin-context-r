package de.invesdwin.context.r.runtime.renjin.pool;

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

import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.renjin.pool.internal.ExtendedPackageLoader;
import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class RenjinScriptEngineObjectPool extends ATimeoutObjectPool<RenjinScriptEngine>
        implements FactoryBean<RenjinScriptEngineObjectPool> {

    public static final RenjinScriptEngineObjectPool INSTANCE = new RenjinScriptEngineObjectPool();
    private static final RenjinScriptEngineFactory FACTORY = new RenjinScriptEngineFactory();

    private RenjinScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void destroyObject(final RenjinScriptEngine obj) {
        obj.getSession().close();
    }

    @Override
    protected RenjinScriptEngine newObject() {
        final RenjinScriptEngine engine = FACTORY.getScriptEngine(
                new SessionBuilder().withDefaultPackages().setPackageLoader(ExtendedPackageLoader.INSTANCE).build());
        engine.getContext().setWriter(new PrintWriter(new Slf4jDebugOutputStream(IScriptTaskRunnerR.LOG)));
        engine.getContext().setErrorWriter(new PrintWriter(new Slf4jWarnOutputStream(IScriptTaskRunnerR.LOG)));
        return engine;
    }

    @Override
    protected void passivateObject(final RenjinScriptEngine obj) {
        try {
            obj.eval(IScriptTaskRunnerR.CLEANUP_SCRIPT);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RenjinScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RenjinScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
