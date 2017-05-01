package de.invesdwin.context.r.optimalf;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.invesdwin.context.r.runtime.contract.IScriptResultExpression;
import de.invesdwin.context.r.runtime.contract.IScriptTask;

@NotThreadSafe
public class OptimalfScriptTask implements IScriptTask {

    private final List<List<Double>> tradesPerStrategy;

    public OptimalfScriptTask(final List<List<Double>> tradesPerStrategy) {
        this.tradesPerStrategy = tradesPerStrategy;
    }

    public List<Double> getOptimalfPerStrategy() {
        //        final RenjinTask task = Renjin.R(true, true)
        //                .code(new InputStreamReader(getClass().getResourceAsStream("OptimalfScriptTask.R")))
        //                .build();
        //        final RenjinResult result = task.execute();
        //        if (!result.success()) {
        //            throw new IllegalStateException("Error: " + result.error(), result.cause());
        //        }
        return null;
    }

    @Override
    public Resource getResource() {
        return new ClassPathResource("OptimalfScriptTask.R", getClass());
    }

    @Override
    public Iterable<IScriptResultExpression<?>> getResultExpressions() {
        return null;
    }

}
