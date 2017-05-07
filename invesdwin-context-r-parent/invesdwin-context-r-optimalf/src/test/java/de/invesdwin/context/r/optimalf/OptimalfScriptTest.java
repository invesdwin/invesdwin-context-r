package de.invesdwin.context.r.optimalf;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;

import de.invesdwin.context.r.runtime.cli.CliScriptTaskRunner;
import de.invesdwin.context.r.runtime.jri.JriScriptTaskRunner;
import de.invesdwin.context.r.runtime.renjin.RenjinScriptTaskRunner;
import de.invesdwin.context.r.runtime.rserve.RserveScriptTaskRunner;
import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class OptimalfScriptTest extends ATest {

    private static final int ITERATIONS = 10;
    @Inject
    private CliScriptTaskRunner cliScriptTaskRunner;
    @Inject
    private RserveScriptTaskRunner rserveScriptTaskRunner;
    @Inject
    private JriScriptTaskRunner jriScriptTaskRunner;
    @Inject
    private RenjinScriptTaskRunner renjinScriptTaskRunner;

    @Test
    public void testCli() {
        for (int i = 0; i < ITERATIONS; i++) {
            new OptimalfScript(cliScriptTaskRunner, null).getOptimalfPerStrategy();
            log.info("------------------------");
        }
    }

    @Test
    public void testRserve() {
        for (int i = 0; i < ITERATIONS; i++) {
            new OptimalfScript(rserveScriptTaskRunner, null).getOptimalfPerStrategy();
            log.info("------------------------");
        }
    }

    @Test
    public void testJri() {
        for (int i = 0; i < ITERATIONS; i++) {
            new OptimalfScript(jriScriptTaskRunner, null).getOptimalfPerStrategy();
            log.info("------------------------");
        }
    }

    @Ignore("org.renjin.eval.EvalException: objective function result has different length than parameter matrix")
    @Test
    public void testRenjin() {
        for (int i = 0; i < ITERATIONS; i++) {
            new OptimalfScript(renjinScriptTaskRunner, null).getOptimalfPerStrategy();
            log.info("------------------------");
        }
    }

}
