package de.invesdwin.context.r.runtime.contract;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

@NotThreadSafe
public abstract class AScriptTask<R> {

    public abstract Resource getScriptResource();

    public String getScriptResourceAsString() {
        try (InputStream in = getScriptResource().getInputStream()) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Reader getScriptResourceAsReader() {
        try {
            return new InputStreamReader(getScriptResource().getInputStream());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void populateInputs(IScriptTaskInputs inputs);

    public abstract R extractResults(IScriptTaskResults results);

    public R run(final IScriptTaskRunner runner) {
        return runner.run(this);
    }

}
