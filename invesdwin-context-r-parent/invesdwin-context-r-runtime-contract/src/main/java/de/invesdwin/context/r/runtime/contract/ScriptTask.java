package de.invesdwin.context.r.runtime.contract;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

@NotThreadSafe
public class ScriptTask {

    private final Resource scriptResource;

    public ScriptTask(final Resource scriptResource) {
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

}
