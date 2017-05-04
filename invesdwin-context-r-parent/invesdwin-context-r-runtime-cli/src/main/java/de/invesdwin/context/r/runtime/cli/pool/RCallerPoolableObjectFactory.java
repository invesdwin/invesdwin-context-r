package de.invesdwin.context.r.runtime.cli.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;
import org.zeroturnaround.exec.stream.slf4j.Slf4jDebugOutputStream;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.log.Log;
import de.invesdwin.context.pool.IPoolableObjectFactory;
import de.invesdwin.context.r.runtime.cli.CliScriptTaskRunner;

@ThreadSafe
@Named
public final class RCallerPoolableObjectFactory
        implements IPoolableObjectFactory<RCaller>, FactoryBean<RCallerPoolableObjectFactory> {

    public static final RCallerPoolableObjectFactory INSTANCE = new RCallerPoolableObjectFactory();
    private static final Log LOG = new Log(CliScriptTaskRunner.class);

    private RCallerPoolableObjectFactory() {}

    @Override
    public RCaller makeObject() {
        final RCaller rCaller = RCaller.create();
        if (LOG.isDebugEnabled()) {
            rCaller.redirectROutputToStream(new Slf4jDebugOutputStream(LOG));
        }
        return rCaller;
    }

    @Override
    public void destroyObject(final RCaller obj) throws Exception {
        obj.StopRCallerOnline();
    }

    @Override
    public boolean validateObject(final RCaller obj) {
        return true;
    }

    @Override
    public void activateObject(final RCaller obj) throws Exception {}

    @Override
    public void passivateObject(final RCaller obj) throws Exception {
        obj.getRCode().clearOnline();
        obj.getRCode().clear();
        obj.getRCode().getCode().insert(0, "rm(list = ls())\n");
        obj.getRCode().addRCode("result <- c()");
        obj.runAndReturnResultOnline("result");
    }

    @Override
    public RCallerPoolableObjectFactory getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RCallerPoolableObjectFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
