package de.invesdwin.context.r.optimalf;

import javax.annotation.concurrent.Immutable;

import org.kohsuke.args4j.CmdLineParser;

import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.beans.init.AMain;
import de.invesdwin.context.r.runtime.contract.ProvidedScriptTaskRunner;

@Immutable
public class Main extends AMain {

    static {
        PlatformInitializerProperties.setAllowed(false);
    }

    protected Main(final String[] args) {
        super(args, false);
    }

    public static void main(final String[] args) {
        new Main(args);
    }

    @Override
    protected void startApplication(final CmdLineParser parser) throws Exception {
        new OptimalfScriptTask(null).run(ProvidedScriptTaskRunner.INSTANCE);
    }

}
