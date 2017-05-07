package de.invesdwin.context.r.optimalf;

import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.r.runtime.contract.AScriptTask;
import de.invesdwin.context.r.runtime.contract.IScriptTaskInputs;
import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;

@NotThreadSafe
public class OptimalfScript {

    private final List<List<Double>> tradesPerStrategy;
    private final IScriptTaskRunner runner;

    public OptimalfScript(final IScriptTaskRunner runner, final List<List<Double>> tradesPerStrategy) {
        this.runner = runner;
        this.tradesPerStrategy = tradesPerStrategy;
    }

    public List<Double> getOptimalfPerStrategy() {
        final AScriptTask scriptTask = new AScriptTask(new ClassPathResource("OptimalfScript.R", getClass())) {
            @Override
            public void populateInputs(final IScriptTaskInputs inputs) {
                inputs.putDoubleMatrix("asd", tradesPerStrategy);
            }
        };
        try (final IScriptTaskResults results = runner.run(scriptTask)) {
            final Double[] optimalf = results.getDoubleVector("optimalf");
            if (optimalf != null) {
                return Arrays.asList(optimalf);
            } else {
                return null;
            }
        }
    }

}
