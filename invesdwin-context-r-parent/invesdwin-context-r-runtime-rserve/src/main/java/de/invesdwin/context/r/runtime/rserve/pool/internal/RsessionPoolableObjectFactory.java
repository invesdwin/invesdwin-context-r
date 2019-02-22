package de.invesdwin.context.r.runtime.rserve.pool.internal;

import java.io.File;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.math.R.Rdaemon;
import org.math.R.RserverConf;
import org.math.R.Rsession;
import org.math.R.StartRserve;
import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.pool.IPoolableObjectFactory;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.rserve.RserveProperties;
import de.invesdwin.context.r.runtime.rserve.RserveScriptTaskEngineR;
import de.invesdwin.context.r.runtime.rserve.RserverConfMode;
import de.invesdwin.context.system.OperatingSystem;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
@Named
public final class RsessionPoolableObjectFactory
        implements IPoolableObjectFactory<Rsession>, FactoryBean<RsessionPoolableObjectFactory> {

    public static final RsessionPoolableObjectFactory INSTANCE = new RsessionPoolableObjectFactory();

    @GuardedBy("this.class")
    private static FDate lastTimestamp = FDate.MIN_DATE;
    @GuardedBy("this.class")
    private static boolean initialized = false;

    private RsessionPoolableObjectFactory() {}

    @Override
    public Rsession makeObject() {
        maybeInitialize();
        final Rsession session = createSession();
        final FDate timestamp = newTimestamp();
        Reflections.field("SINK_FILE")
                .ofType(String.class)
                .in(session)
                .set(".Rout-" + timestamp.toString(FDate.FORMAT_TIMESTAMP_UNDERSCORE));
        return session;
    }

    private static synchronized FDate newTimestamp() {
        FDate timestamp = new FDate();
        while (timestamp.equals(lastTimestamp)) {
            try {
                FTimeUnit.MILLISECONDS.sleep(1);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
            timestamp = new FDate();
        }
        lastTimestamp = timestamp;
        return timestamp;
    }

    private Rsession createSession() {
        switch (RserveProperties.RSERVER_CONF_MODE) {
        case LOCAL_SPAWN:
            return Rsession.newInstanceTry(new RsessionLogger(), RserveProperties.RSERVER_CONF);
        case LOCAL:
            //fallthrough
        case REMOTE:
            return Rsession.newRemoteInstance(new RsessionLogger(), RserveProperties.RSERVER_CONF);
        default:
            throw UnknownArgumentException.newInstance(RserverConfMode.class, RserveProperties.RSERVER_CONF_MODE);
        }
    }

    /**
     * Need to install rserve ourselves so that we can use the updated repo. Rsession sadly does not allow to modify the
     * url in a different way.
     */
    private static synchronized void maybeInitialize() {
        if (initialized) {
            return;
        }
        final RserverConf rServeConf = RserveProperties.RSERVER_CONF;

        String http_proxy = null;
        if (rServeConf != null && rServeConf.properties != null && rServeConf.properties.containsKey("http_proxy")) {
            http_proxy = rServeConf.properties.getProperty("http_proxy");
        }

        Rdaemon.findR_HOME(Rdaemon.R_HOME);
        final String exeSuffix = OperatingSystem.isWindows() ? ".exe" : "";
        boolean rserveInstalled = StartRserve
                .isRserveInstalled(Rdaemon.R_HOME + File.separator + "bin" + File.separator + "R" + exeSuffix);
        if (!rserveInstalled) {
            rserveInstalled = StartRserve.installRserve(
                    Rdaemon.R_HOME + File.separator + "bin" + File.separator + "R" + exeSuffix, http_proxy,
                    RserveProperties.RSERVER_REPOSITORY);
            if (!rserveInstalled) {
                final String notice = "Please install Rserve manually in your R environment using \"install.packages('Rserve',,'"
                        + RserveProperties.RSERVER_REPOSITORY + "')\" command.";
                throw new RuntimeException(notice);
            }
        }
        initialized = true;
    }

    @Override
    public void destroyObject(final Rsession obj) throws Exception {
        obj.end();
    }

    @Override
    public boolean validateObject(final Rsession obj) {
        return true;
    }

    @Override
    public void activateObject(final Rsession obj) throws Exception {}

    @Override
    public void passivateObject(final Rsession obj) throws Exception {
        final RserveScriptTaskEngineR engine = new RserveScriptTaskEngineR(obj);
        engine.eval(IScriptTaskRunnerR.CLEANUP_SCRIPT);
        engine.close();
        obj.close(); //reset logger
    }

    @Override
    public RsessionPoolableObjectFactory getObject() throws Exception {
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
