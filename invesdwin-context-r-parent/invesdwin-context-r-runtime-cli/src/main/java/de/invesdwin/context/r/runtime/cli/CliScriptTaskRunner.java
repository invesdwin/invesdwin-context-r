package de.invesdwin.context.r.runtime.cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import com.github.rcaller.rstuff.RCaller;

import de.invesdwin.context.r.runtime.cli.pool.RCallerObjectPool;
import de.invesdwin.context.r.runtime.contract.IScriptResultExpression;
import de.invesdwin.context.r.runtime.contract.IScriptResults;
import de.invesdwin.context.r.runtime.contract.IScriptTask;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;

@Immutable
@Named
public class CliScriptTaskRunner implements IScriptTaskRunner {

    @Override
    public IScriptResults run(final IScriptTask task) {

        final Resource resource = task.getResource();
        final Iterable<IScriptResultExpression<?>> expressions = task.getResultExpressions();

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

}
