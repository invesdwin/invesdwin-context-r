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
public abstract class AScriptTask {

    private final Resource scriptResource;

    public AScriptTask(final Resource scriptResource) {
        this.scriptResource = scriptResource;
    }

    public Resource getScriptResource() {
        return scriptResource;
    }

    public String getScriptResourceAsString() {
        try (InputStream in = scriptResource.getInputStream()) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Reader getScriptResourceAsReader() {
        try {
            return new InputStreamReader(scriptResource.getInputStream());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void populateInputs(IScriptTaskInputs inputs);

}
