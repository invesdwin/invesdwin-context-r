package de.invesdwin.context.r.runtime.contract;

import javax.annotation.concurrent.NotThreadSafe;

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

}
