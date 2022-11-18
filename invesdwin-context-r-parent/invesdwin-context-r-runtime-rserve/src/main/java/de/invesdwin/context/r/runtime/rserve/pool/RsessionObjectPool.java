package de.invesdwin.context.r.runtime.rserve.pool;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.rserve.RserveProperties;
import de.invesdwin.context.r.runtime.rserve.RserveScriptTaskEngineR;
import de.invesdwin.context.r.runtime.rserve.RserverConfMode;
import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class RsessionObjectPool extends ATimeoutObjectPool<ExtendedRserveSession>
        implements FactoryBean<RsessionObjectPool> {

    public static final RsessionObjectPool INSTANCE = new RsessionObjectPool();
    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator();

    private RsessionObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final ExtendedRserveSession element) {
        element.end();
    }

    @Override
    protected ExtendedRserveSession newObject() {
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
            return new ExtendedRserveSession(new de.invesdwin.context.r.runtime.rserve.pool.internal.RsessionLogger(),
                    RserveProperties.RSERVER_CONF, true);
        case LOCAL:
            //fallthrough
        case REMOTE:
            return new ExtendedRserveSession(new de.invesdwin.context.r.runtime.rserve.pool.internal.RsessionLogger(),
                    RserveProperties.RSERVER_CONF, false);
        default:
            throw UnknownArgumentException.newInstance(RserverConfMode.class, RserveProperties.RSERVER_CONF_MODE);
        }
    }

    @Override
    protected void passivateObject(final ExtendedRserveSession element) {
        final RserveScriptTaskEngineR engine = new RserveScriptTaskEngineR(element);
        engine.eval(IScriptTaskRunnerR.CLEANUP_SCRIPT);
        engine.close();
        element.closeLog(); //reset logger
    }

    @Override
    public RsessionObjectPool getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RsessionObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
