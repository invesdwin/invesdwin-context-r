package de.invesdwin.context.r.runtime.cli.pool.internal;

import javax.annotation.concurrent.NotThreadSafe;

import org.fest.reflect.field.Invoker;

import com.github.rcaller.EventHandler;
import com.github.rcaller.MessageSaver;
import com.github.rcaller.TempFileService;
import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCallerOptions;
import com.github.rcaller.rstuff.RCode;
import com.github.rcaller.rstuff.ROutputParser;
import com.github.rcaller.rstuff.RStreamHandler;

import de.invesdwin.context.r.runtime.contract.IScriptTaskRunner;
import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.Strings;

@NotThreadSafe
public class ModifiedRCaller extends RCaller {

    private final TempFileService tempFileServiceCopy;
    private final MessageSaver errorMessageSaverCopy;

    public ModifiedRCaller() {
        super(null, new ROutputParser(), newOutputStreamHandler(), newErrorStreamHandler(), new MessageSaver(),
                new ModifiedTempFileService(), RCallerOptions.create());
        final Invoker<TempFileService> rcallerTempFileServiceField = Reflections.field("tempFileService")
                .ofType(TempFileService.class)
                .in(this);
        this.tempFileServiceCopy = rcallerTempFileServiceField.get();
        final Invoker<MessageSaver> rcallerErrorMessageSaverField = Reflections.field("errorMessageSaver")
                .ofType(MessageSaver.class)
                .in(this);
        this.errorMessageSaverCopy = rcallerErrorMessageSaverField.get();
        setRCode(RCode.create());
    }

    private static RStreamHandler newOutputStreamHandler() {
        final RStreamHandler output = new RStreamHandler(null, "Output");
        output.addEventHandler(new EventHandler() {
            @Override
            public void messageReceived(final String senderName, final String msg) {
                IScriptTaskRunner.LOG.debug(msg);
            }
        });
        return output;
    }

    private static RStreamHandler newErrorStreamHandler() {
        final RStreamHandler error = new RStreamHandler(null, "Error");
        error.addEventHandler(new EventHandler() {
            @Override
            public void messageReceived(final String senderName, final String msg) {
                if (Strings.containsIgnoreCase(msg, "error")) {
                    IScriptTaskRunner.LOG.error(msg);
                } else {
                    IScriptTaskRunner.LOG.warn(msg);
                }
            }
        });
        return error;
    }

    @Override
    public void setRCode(final RCode rcode) {
        super.setRCode(modifyRCode(rcode));
    }

    private RCode modifyRCode(final RCode rcode) {
        final Invoker<TempFileService> rcodeTempFileServiceField = Reflections.field("tempFileService")
                .ofType(TempFileService.class)
                .in(rcode);
        final TempFileService existingTempFileService = rcodeTempFileServiceField.get();
        if (existingTempFileService != null) {
            existingTempFileService.deleteRCallerTempFiles();
        }
        rcodeTempFileServiceField.set(tempFileServiceCopy);
        return rcode;
    }

    @Override
    public void deleteTempFiles() {
        super.deleteTempFiles();
        errorMessageSaverCopy.resetMessage();
    }

}
