package de.invesdwin.context.r.runtime.renjin.pool.internal;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.renjin.eval.SessionBuilder;
import org.renjin.script.RenjinScriptEngine;
import org.renjin.script.RenjinScriptEngineFactory;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.pool.IPoolableObjectFactory;

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
        return engine;
    }

    @Override
    public void destroyObject(final RenjinScriptEngine obj) throws Exception {
        obj.getSession().close();
    }

    @Override
    public boolean validateObject(final RenjinScriptEngine obj) {
        return true;
    }

    @Override
    public void activateObject(final RenjinScriptEngine obj) throws Exception {}

    @Override
    public void passivateObject(final RenjinScriptEngine obj) throws Exception {
        obj.eval("rm(list = ls())");
    }

    @Override
    public RenjinScriptEnginePoolableObjectFactory getObject() throws Exception {
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
