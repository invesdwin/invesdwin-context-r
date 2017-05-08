package de.invesdwin.context.r.runtime.contract;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.Strings;

/**
 * This instance will use the IScriptTaskRunner that was chosen by the user either by including the appropriate runtime
 * module in the classpath or by defining the class to be used as a system property.
 */
@Immutable
@Named
public final class ProvidedScriptTaskRunner implements IScriptTaskRunner, FactoryBean<ProvidedScriptTaskRunner> {

    public static final String PROVIDED_INSTANCE_KEY = IScriptTaskRunner.class.getName();

    public static final ProvidedScriptTaskRunner INSTANCE = new ProvidedScriptTaskRunner();

    @GuardedBy("this.class")
    private static IScriptTaskRunner providedInstance;

    private ProvidedScriptTaskRunner() {}

    public static synchronized IScriptTaskRunner getProvidedInstance() {
        if (providedInstance == null) {
            final SystemProperties systemProperties = new SystemProperties();
            if (systemProperties.containsValue(PROVIDED_INSTANCE_KEY)) {
                try {
                    final String runner = systemProperties.getString(PROVIDED_INSTANCE_KEY);
                    return (IScriptTaskRunner) Reflections.classForName(runner).newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                final Map<String, IScriptTaskRunner> runners = new LinkedHashMap<String, IScriptTaskRunner>();
                for (final IScriptTaskRunner runner : ServiceLoader.load(IScriptTaskRunner.class)) {
                    final IScriptTaskRunner existing = runners.put(runner.getClass().getName(), runner);
                    if (existing != null) {
                        throw new IllegalStateException("Duplicate service provider found for [" + PROVIDED_INSTANCE_KEY + "="
                                + existing.getClass().getName()
                                + "]. Please make sure you have only one provider for it in the classpath.");
                    }
                }
                if (runners.isEmpty()) {
                    throw new IllegalStateException("No service provider found for [" + PROVIDED_INSTANCE_KEY
                            + "]. Please add one provider for it to the classpath.");
                }
                if (runners.size() > 1) {
                    final StringBuilder runnersStr = new StringBuilder("(");
                    for (final String runner : runners.keySet()) {
                        runnersStr.append(runner);
                        runnersStr.append("|");
                    }
                    Strings.removeEnd(runnersStr, "|");
                    runnersStr.append(")");
                    throw new IllegalStateException(
                            "More than one service provider found for [" + PROVIDED_INSTANCE_KEY + "=" + runnersStr
                                    + "] to choose from. Please remove unwanted ones from the classpath or choose a "
                                    + "specific one by defining a system property for the preferred one. E.g. on the command line with -D"
                                    + PROVIDED_INSTANCE_KEY + "=" + runners.keySet().iterator().next());
                }
                setProvidedInstance(runners.values().iterator().next());
            }
        }
        return providedInstance;
    }

    public static synchronized void setProvidedInstance(final IScriptTaskRunner providedInstance) {
        ProvidedScriptTaskRunner.providedInstance = providedInstance;
        final SystemProperties systemProperties = new SystemProperties();
        systemProperties.setString(PROVIDED_INSTANCE_KEY, providedInstance.getClass().getName());
    }

    @Override
    public <T> T run(final AScriptTask<T> scriptTask) {
        return getProvidedInstance().run(scriptTask);
    }

    @Override
    public Class<?> getObjectType() {
        return ProvidedScriptTaskRunner.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public ProvidedScriptTaskRunner getObject() throws Exception {
        return INSTANCE;
    }

}
