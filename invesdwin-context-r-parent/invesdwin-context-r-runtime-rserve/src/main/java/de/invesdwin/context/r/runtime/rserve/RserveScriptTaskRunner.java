package de.invesdwin.context.r.runtime.rserve;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.math.R.Rsession;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.context.r.runtime.contract.ScriptTask;
import de.invesdwin.context.r.runtime.rserve.pool.RsessionObjectPool;

@Immutable
@Named
public final class RserveScriptTaskRunner implements IScriptTaskRunner, FactoryBean<RserveScriptTaskRunner> {

    public static final RserveScriptTaskRunner INSTANCE = new RserveScriptTaskRunner();

    private RserveScriptTaskRunner() {}

    @Override
    public IScriptTaskResults run(final ScriptTask scriptTask) {

        final Resource resource = scriptTask.getScriptResource();

        try (InputStream in = resource.getInputStream()) {
            try {
                final Rsession caller = RsessionObjectPool.INSTANCE.borrowObject();
                caller.voidEval(IOUtils.toString(in, StandardCharsets.UTF_8));
                RsessionObjectPool.INSTANCE.returnObject(caller);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public RserveScriptTaskRunner getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return RserveScriptTaskRunner.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
