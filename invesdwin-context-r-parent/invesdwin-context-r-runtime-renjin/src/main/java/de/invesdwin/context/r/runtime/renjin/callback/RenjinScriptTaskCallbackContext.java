package de.invesdwin.context.r.runtime.renjin.callback;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import de.invesdwin.context.integration.marshaller.MarshallerJsonJackson;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.r.runtime.contract.callback.ScriptTaskParametersRFromJson;
import de.invesdwin.context.r.runtime.contract.callback.ScriptTaskParametersRFromJsonPool;
import de.invesdwin.context.r.runtime.contract.callback.ScriptTaskReturnsRToExpression;
import de.invesdwin.context.r.runtime.contract.callback.ScriptTaskReturnsRToExpressionPool;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.UUIDs;

@ThreadSafe
public class RenjinScriptTaskCallbackContext implements Closeable {

    private static final Map<String, RenjinScriptTaskCallbackContext> UUID_CONTEXT = new ConcurrentHashMap<>();

    private final String uuid;
    private final IScriptTaskCallback callback;
    private final ObjectMapper mapper;

    public RenjinScriptTaskCallbackContext(final IScriptTaskCallback callback) {
        this.uuid = UUIDs.newPseudoRandomUUID();
        this.callback = callback;
        UUID_CONTEXT.put(uuid, this);
        this.mapper = MarshallerJsonJackson.getInstance().getJsonMapper(false);
    }

    public static RenjinScriptTaskCallbackContext getContext(final String uuid) {
        return UUID_CONTEXT.get(uuid);
    }

    public void init(final IScriptTaskEngine engine) {
        engine.getInputs().putString("renjinScriptTaskCallbackContextUuid", getUuid());
        engine.eval(new ClassPathResource(RenjinScriptTaskCallbackContext.class.getSimpleName() + ".R",
                RenjinScriptTaskCallbackContext.class));
    }

    public String getUuid() {
        return uuid;
    }

    public String invoke(final String methodName, final String dims, final String args) {
        final ScriptTaskParametersRFromJson parameters = ScriptTaskParametersRFromJsonPool.INSTANCE.borrowObject();
        final ScriptTaskReturnsRToExpression returns = ScriptTaskReturnsRToExpressionPool.INSTANCE.borrowObject();
        try {
            final JsonNode jsonDims = toJsonNode(dims);
            final JsonNode jsonArgs = toJsonNode(args);
            parameters.setParameters(jsonDims, jsonArgs);
            callback.invoke(methodName, parameters, returns);
            return returns.getReturnExpression();
        } finally {
            ScriptTaskReturnsRToExpressionPool.INSTANCE.returnObject(returns);
            ScriptTaskParametersRFromJsonPool.INSTANCE.returnObject(parameters);
        }
    }

    private JsonNode toJsonNode(final String json) {
        try {
            final JsonNode node = mapper.readTree(json);
            if (node instanceof NullNode) {
                return null;
            } else {
                return node;
            }
        } catch (final Throwable t) {
            throw Throwables.propagate(t);
        }
    }

    @Override
    public void close() {
        UUID_CONTEXT.remove(uuid);
    }

}
