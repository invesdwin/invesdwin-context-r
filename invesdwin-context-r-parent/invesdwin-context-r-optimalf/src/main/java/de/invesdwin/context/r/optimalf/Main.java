package de.invesdwin.context.r.optimalf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.kohsuke.args4j.CmdLineParser;

import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.beans.init.AMain;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.Decimal;

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
        final List<List<Double>> tradesPerStrategy = new ArrayList<List<Double>>();
        final ArrayList<Double> tradesPerStrategy1 = new ArrayList<Double>();
        tradesPerStrategy.add(tradesPerStrategy1);
        tradesPerStrategy1.add(2D);
        tradesPerStrategy1.add(-1D);
        final List<Double> run = new OptimalfScriptTask(tradesPerStrategy).run();
        Assertions.checkEquals(run.size(), 1);
        Assertions.assertThat(new Decimal(run.get(0)).round(2)).isEqualTo(new Decimal("0.25"));
    }

}
