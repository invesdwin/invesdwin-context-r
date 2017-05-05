package de.invesdwin.context.r.runtime.rserve.pool.internal;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.log.Log;
import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;

@ThreadSafe
public final class RsessionLogger implements org.math.R.Logger {

    public static final RsessionLogger INSTANCE = new RsessionLogger();

    private static final Log LOGGER = IScriptTaskRunner.LOG;

    private RsessionLogger() {}

    @Override
    public void println(final String text, final Level level) {
        switch (level) {
        case OUTPUT:
            //fallthrough
        case INFO:
            LOGGER.debug(text);
            break;
        case WARNING:
            LOGGER.warn(text);
            break;
        case ERROR:
            LOGGER.error(text);
            break;
        default:
            LOGGER.trace(text);
            break;
        }
    }

    @Override
    public void close() {}

}
