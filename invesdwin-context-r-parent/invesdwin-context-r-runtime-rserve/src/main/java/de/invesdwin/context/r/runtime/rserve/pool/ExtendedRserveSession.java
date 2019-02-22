package de.invesdwin.context.r.runtime.rserve.pool;

import javax.annotation.concurrent.NotThreadSafe;

import org.math.R.RLog;
import org.math.R.RserveSession;
import org.math.R.RserverConf;
import org.rosuda.REngine.REXP;

@NotThreadSafe
public class ExtendedRserveSession extends RserveSession {

    public ExtendedRserveSession(final RLog log, final RserverConf serverconf, final boolean tryLocalRServe) {
        super(log, serverconf, tryLocalRServe);
    }

    @Override
    public REXP rawEval(final String expression) {
        return (REXP) super.rawEval(expression);
    }

    @Override
    public REXP rawEval(final String expression, final boolean tryEval) {
        return (REXP) super.rawEval(expression, tryEval);
    }
}
