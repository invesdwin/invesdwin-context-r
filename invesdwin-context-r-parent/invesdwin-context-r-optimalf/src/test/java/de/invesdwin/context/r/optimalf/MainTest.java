package de.invesdwin.context.r.optimalf;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.context.r.runtime.contract.ProvidedScriptTaskRunnerR;
import de.invesdwin.context.r.runtime.rcaller.RCallerScriptTaskRunnerR;

@NotThreadSafe
public class MainTest {

    @Test
    public void test() {
        Main.main(new String[] { "-D" + ProvidedScriptTaskRunnerR.PROVIDED_INSTANCE_KEY + "="
                + RCallerScriptTaskRunnerR.class.getName() });
    }

}
