package de.invesdwin.context.r.optimalf;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;

import org.junit.Test;

import de.invesdwin.context.r.runtime.cli.CliScriptTaskRunner;
import de.invesdwin.context.r.runtime.rserve.RserveScriptTaskRunner;
import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class OptimalfScriptTest extends ATest {

    private static final int ITERATIONS = 10;
    @Inject
    private CliScriptTaskRunner cliScriptTaskRunner;
    @Inject
    private RserveScriptTaskRunner rserveScriptTaskRunner;

    @Test
    public void testCli() {
        for (int i = 0; i < ITERATIONS; i++) {
            new OptimalfScript(cliScriptTaskRunner, null).getOptimalfPerStrategy();
        }
    }

    @Test
    public void testRserve() {
        for (int i = 0; i < ITERATIONS; i++) {
            new OptimalfScript(rserveScriptTaskRunner, null).getOptimalfPerStrategy();
        }
    }

}
