package de.invesdwin.context.r.runtime.rserve.pool.internal;

import java.io.File;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.pool.IPoolableObjectFactory;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.rserve.RserveProperties;
import de.invesdwin.context.r.runtime.rserve.RserveScriptTaskEngineR;
import de.invesdwin.context.r.runtime.rserve.RserverConfMode;
import de.invesdwin.context.r.runtime.rserve.pool.ExtendedRserveSession;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.UniqueNameGenerator;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.time.date.FDate;

@ThreadSafe
@Named
public final class RsessionPoolableObjectFactory
        implements IPoolableObjectFactory<ExtendedRserveSession>, FactoryBean<RsessionPoolableObjectFactory> {

    public static final RsessionPoolableObjectFactory INSTANCE = new RsessionPoolableObjectFactory();
    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator();

    @GuardedBy("this.class")
    private static FDate lastTimestamp = FDate.MIN_DATE;

    private RsessionPoolableObjectFactory() {}

    @Override
    public ExtendedRserveSession makeObject() {
        final ExtendedRserveSession session = createSession();
        /*
         * use absolute path or else connection might not work properly, also use unique name to prevent multiple
         * instances from sharing the same file
         */
        final String sinkFile = new File(ContextProperties.TEMP_DIRECTORY, UNIQUE_NAME_GENERATOR.get(".Rout"))
                .getAbsolutePath();
        Reflections.field("SINK_FILE").ofType(String.class).in(session).set(sinkFile);
        //https://github.com/yannrichet/rsession/issues/21 not using sink is a lot faster, errors are still properly reported
        session.sinkOutput(false);
        session.sinkMessage(false);
        return session;
    }

    private ExtendedRserveSession createSession() {
        switch (RserveProperties.RSERVER_CONF_MODE) {
        case LOCAL_SPAWN:
            return new ExtendedRserveSession(new RsessionLogger(), RserveProperties.RSERVER_CONF, true);
        case LOCAL:
            //fallthrough
        case REMOTE:
            return new ExtendedRserveSession(new RsessionLogger(), RserveProperties.RSERVER_CONF, false);
        default:
            throw UnknownArgumentException.newInstance(RserverConfMode.class, RserveProperties.RSERVER_CONF_MODE);
        }
    }

    @Override
    public void destroyObject(final ExtendedRserveSession obj) {
        obj.end();
    }

    @Override
    public boolean validateObject(final ExtendedRserveSession obj) {
        return true;
    }

    @Override
    public void activateObject(final ExtendedRserveSession obj) {}

    @Override
    public void passivateObject(final ExtendedRserveSession obj) {
        final RserveScriptTaskEngineR engine = new RserveScriptTaskEngineR(obj);
        engine.eval(IScriptTaskRunnerR.CLEANUP_SCRIPT);
        engine.close();
        obj.closeLog(); //reset logger
    }

    @Override
    public RsessionPoolableObjectFactory getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RsessionPoolableObjectFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
