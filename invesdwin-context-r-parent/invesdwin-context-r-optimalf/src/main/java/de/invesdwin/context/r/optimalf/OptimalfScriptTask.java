package de.invesdwin.context.r.optimalf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.context.r.runtime.contract.AScriptTaskR;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.stream.DecimalStreamAvg;
import de.invesdwin.util.math.decimal.stream.DecimalStreamGeomAvg;

@NotThreadSafe
public class OptimalfScriptTask extends AScriptTaskR<List<Double>> {

    private final List<List<Double>> includedTradesPerStrategy;
    private final List<Boolean> includeStrategy;
    private final List<Double> predefinedStrategyResult;

    /**
     * The input vectors should only be of the size that the actual trades were of.
     */
    public OptimalfScriptTask(final List<? extends List<Double>> tradesPerStrategy) {
        this.includeStrategy = new ArrayList<>(tradesPerStrategy.size());
        this.includedTradesPerStrategy = new ArrayList<>(tradesPerStrategy.size());
        this.predefinedStrategyResult = new ArrayList<>(tradesPerStrategy.size());
        for (final List<Double> trades : tradesPerStrategy) {
            final DecimalStreamGeomAvg<Decimal> ghpr = new DecimalStreamGeomAvg<Decimal>(Decimal.ZERO);
            final DecimalStreamAvg<Decimal> ahpr = new DecimalStreamAvg<Decimal>(Decimal.ZERO);
            boolean negativeTradeFound = false;
            for (final Double trade : trades) {
                final Decimal tradeDecimal = new Decimal(trade);
                if (tradeDecimal.isNegative()) {
                    negativeTradeFound = true;
                }
                ghpr.process(tradeDecimal);
                ahpr.process(tradeDecimal);
            }
            if (!negativeTradeFound || (tradesPerStrategy.size() > 1
                    && (ghpr.getGeomAvg().isNegativeOrZero() || ahpr.getAvg().isNegativeOrZero()))) {
                includeStrategy.add(false);
                if (negativeTradeFound) {
                    //remove unprofitable strategies
                    predefinedStrategyResult.add(0D);
                } else {
                    //always winning has infinite upside potential
                    predefinedStrategyResult.add(0.999);
                }
            } else {
                includeStrategy.add(true);
                includedTradesPerStrategy.add(trades);
                predefinedStrategyResult.add(null);
            }
        }

    }

    @Override
    public void populateInputs(final IScriptTaskInputs inputs) {
        if (includedTradesPerStrategy.isEmpty()) {
            return;
        }
        int maxTradesCount = 0;
        for (int i = 0; i < includedTradesPerStrategy.size(); i++) {
            final List<Double> strategy = includedTradesPerStrategy.get(i);
            maxTradesCount = Integers.max(maxTradesCount, strategy.size());
        }
        final double[][] tradesMatrix = new double[includedTradesPerStrategy.size()][];
        final double[][] probabilitiesMatrix = new double[includedTradesPerStrategy.size()][];
        for (int strategyIdx = 0; strategyIdx < includedTradesPerStrategy.size(); strategyIdx++) {
            final List<Double> strategy = includedTradesPerStrategy.get(strategyIdx);
            /*
             * we fill up the missing trades on the matrix with 0 trades having 0 probability, so the matrix has uniform
             * size per vector
             */
            final double[] tradesVector = new double[maxTradesCount + 1];
            final double[] probabilitiesVector = new double[maxTradesCount + 1];
            final double tradesCount = strategy.size();
            final double tradeProbability = 1D / tradesCount;
            for (int tradeIdx = 0; tradeIdx < tradesCount; tradeIdx++) {
                final double trade = strategy.get(tradeIdx);
                tradesVector[tradeIdx] = trade;
                probabilitiesVector[tradeIdx] = tradeProbability;
            }
            tradesMatrix[strategyIdx] = tradesVector;
            probabilitiesMatrix[strategyIdx] = probabilitiesVector;
        }
        inputs.putDoubleMatrix("trades", tradesMatrix);
        inputs.putExpression("trades", "t(trades)");
        inputs.putDoubleMatrix("probabilities", probabilitiesMatrix);
        inputs.putExpression("probabilities", "t(probabilities)");
    }

    @Override
    public void executeScript(final IScriptTaskEngine engine) {
        if (includedTradesPerStrategy.isEmpty()) {
            return;
        }
        engine.eval(new ClassPathResource(OptimalfScriptTask.class.getSimpleName() + ".R", getClass()));
    }

    @Override
    public List<Double> extractResults(final IScriptTaskResults results) {
        if (includedTradesPerStrategy.isEmpty()) {
            return newDisabledResult();
        }
        final double profit = results.getDouble("profit");
        if (profit <= 0) {
            //disable trading if resulting profit is negative
            return newDisabledResult();
        } else {
            //add back the excluded strategies to the results
            final List<Double> calculatedOptimalFPerStrategy = results.getDoubleVectorAsList("optimalf");
            final List<Double> optimalFPerStrategy = new ArrayList<>(includeStrategy.size());
            for (int strategyIdx = 0, includedStrategyIdx = 0; strategyIdx < includeStrategy.size(); strategyIdx++) {
                if (includeStrategy.get(strategyIdx)) {
                    final Double calculatedOptimalF = calculatedOptimalFPerStrategy.get(includedStrategyIdx);
                    optimalFPerStrategy.add(calculatedOptimalF);
                    includedStrategyIdx++;
                } else {
                    optimalFPerStrategy.add(predefinedStrategyResult.get(strategyIdx));
                }
            }
            return optimalFPerStrategy;
        }
    }

    private List<Double> newDisabledResult() {
        final List<Double> optimalFs = new ArrayList<Double>(includeStrategy.size());
        for (int strategyIdx = 0; strategyIdx < includeStrategy.size(); strategyIdx++) {
            final Double predefinedResult = predefinedStrategyResult.get(strategyIdx);
            if (predefinedResult != null) {
                optimalFs.add(predefinedResult);
            } else {
                optimalFs.add(0D);
            }
        }
        return optimalFs;
    }

}
