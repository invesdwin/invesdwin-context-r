import(de.invesdwin.context.renjin.callback.RenjinScriptTaskCallbackContext)
callback <- function(methodName, ...) {
    if(!exists("renjinScriptTaskCallbackContext")) {
        if(exists("renjinScriptTaskCallbackContextUuid")) {
            .GlobalEnv$renjinScriptTaskCallbackContext <- RenjinScriptTaskCallbackContext$getContext(renjinScriptTaskCallbackContextUuid))
        } else {
            stop("IScriptTaskCallback not available")
        }
    }
    returnValue = renjinScriptTaskCallbackContext.invoke(methodName, ...)
    if(returnValue.isReturnExpression()) {
    	return(eval(returnValue.getReturnValue()))
    } else {
        return(returnValue.getReturnValue())
    }
}