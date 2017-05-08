package de.invesdwin.context.r.optimalf;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.context.r.runtime.cli.CliScriptTaskRunner;
import de.invesdwin.context.r.runtime.contract.ProvidedScriptTaskRunner;

@NotThreadSafe
public class MainTest {

    @Test
    public void test() {
        Main.main(new String[] {
                "-D" + ProvidedScriptTaskRunner.PROVIDED_INSTANCE_KEY + "=" + CliScriptTaskRunner.class.getName() });
    }

}
