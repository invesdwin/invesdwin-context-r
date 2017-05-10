package de.invesdwin.context.r.optimalf;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.context.r.runtime.contract.AScriptTaskR;

@NotThreadSafe
public class OptimalfScriptTask extends AScriptTaskR<List<Double>> {

    private final List<? extends List<Double>> tradesPerStrategy;

    public OptimalfScriptTask(final List<? extends List<Double>> tradesPerStrategy) {
        this.tradesPerStrategy = tradesPerStrategy;
    }

    @Override
    public void populateInputs(final IScriptTaskInputs inputs) {
        inputs.putDoubleMatrixAsList("asd", tradesPerStrategy);
    }

    @Override
    public void executeScript(final IScriptTaskEngine engine) {
        engine.eval(new ClassPathResource(OptimalfScriptTask.class.getSimpleName() + ".R", getClass()));
    }

    @Override
    public List<Double> extractResults(final IScriptTaskResults results) {
        return results.getDoubleVectorAsList("optimalf");
    }

}
