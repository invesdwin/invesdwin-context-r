package de.invesdwin.context.r.optimalf;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;

import org.junit.Test;

import de.invesdwin.context.r.runtime.cli.CliScriptTaskRunner;
import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class OptimalfScriptTaskTest extends ATest {

    @Inject
    private CliScriptTaskRunner scriptTaskRunner;

    @Test
    public void testCli() {
        for (int i = 0; i < 10; i++) {
            scriptTaskRunner.run(new OptimalfScriptTask(null));
        }
    }

}
