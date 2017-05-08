package de.invesdwin.context.r.runtime.contract;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Named;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.stub.StubSupport;

@Named
@NotThreadSafe
public class ProvidedScriptTaskRunnerStub extends StubSupport {

    @Override
    public void tearDownOnce(final ATest test) throws Exception {
        ProvidedScriptTaskRunner.setProvidedInstance(null);
    }

}
