package de.invesdwin.context.r.optimalf;

import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.context.r.runtime.contract.ScriptTask;

@NotThreadSafe
public class OptimalfScript {

    private final List<List<Double>> tradesPerStrategy;
    private final IScriptTaskRunner runner;

    public OptimalfScript(final IScriptTaskRunner runner, final List<List<Double>> tradesPerStrategy) {
        this.runner = runner;
        this.tradesPerStrategy = tradesPerStrategy;
    }

    public List<Double> getOptimalfPerStrategy() {
        final ScriptTask scriptTask = new ScriptTask(new ClassPathResource("OptimalfScript.R", getClass()));
        try (final IScriptTaskResults results = runner.run(scriptTask)) {
            final Double[] optimalf = results.getDoubleVector("optimalf");
            return Arrays.asList(optimalf);
        }
    }

}
