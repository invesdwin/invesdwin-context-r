package de.invesdwin.context.r.optimalf;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.invesdwin.context.r.runtime.contract.AScriptTask;
import de.invesdwin.context.r.runtime.contract.IScriptTaskInputs;
import de.invesdwin.context.r.runtime.contract.IScriptTaskResults;

@NotThreadSafe
public class OptimalfScriptTask extends AScriptTask<List<Double>> {

    private final List<List<Double>> tradesPerStrategy;

    public OptimalfScriptTask(final List<List<Double>> tradesPerStrategy) {
        this.tradesPerStrategy = tradesPerStrategy;
    }

    @Override
    public Resource getScriptResource() {
        return new ClassPathResource(OptimalfScriptTask.class.getSimpleName() + ".R", getClass());
    }

    @Override
    public void populateInputs(final IScriptTaskInputs inputs) {
        inputs.putDoubleMatrixAsList("asd", tradesPerStrategy);
    }

    @Override
    public List<Double> extractResults(final IScriptTaskResults results) {
        return results.getDoubleVectorAsList("optimalf");
    }

}
