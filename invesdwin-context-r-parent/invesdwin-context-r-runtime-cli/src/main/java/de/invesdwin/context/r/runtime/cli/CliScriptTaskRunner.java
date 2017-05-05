package de.invesdwin.context.r.runtime.cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.cli.pool.RCallerObjectPool;
import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.context.r.runtime.contract.ScriptTask;

@Immutable
@Named
public final class CliScriptTaskRunner implements IScriptTaskRunner, FactoryBean<CliScriptTaskRunner> {

    public static final CliScriptTaskRunner INSTANCE = new CliScriptTaskRunner();

    private CliScriptTaskRunner() {}

    @Override
    public IScriptTaskResults run(final ScriptTask scriptTask) {

        final Resource resource = scriptTask.getScriptResource();

        try (InputStream in = resource.getInputStream()) {
            try {
                final RCaller caller = RCallerObjectPool.INSTANCE.borrowObject();
                caller.getRCode().addRCode(IOUtils.toString(in, StandardCharsets.UTF_8));
                caller.runAndReturnResultOnline("result");
                RCallerObjectPool.INSTANCE.returnObject(caller);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public CliScriptTaskRunner getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return CliScriptTaskRunner.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
