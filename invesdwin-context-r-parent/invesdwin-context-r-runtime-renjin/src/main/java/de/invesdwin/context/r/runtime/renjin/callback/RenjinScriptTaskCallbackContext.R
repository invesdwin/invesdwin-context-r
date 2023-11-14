import(de.invesdwin.context.r.runtime.renjin.callback.RenjinScriptTaskCallbackContext)
callback <- function(methodName, ...) {
    if(!exists("renjinScriptTaskCallbackContext")) {
        if(exists("renjinScriptTaskCallbackContextUuid")) {
            .GlobalEnv$renjinScriptTaskCallbackContext <- RenjinScriptTaskCallbackContext$getContext(renjinScriptTaskCallbackContextUuid)
        } else {
            stop("IScriptTaskCallback not available")
        }
    }
    returnValue = renjinScriptTaskCallbackContext$invoke(methodName, ...)
    print(returnValue$isReturnExpression())
    if(returnValue$isReturnExpression()) {
    	return(eval(parse(text=returnValue$getReturnValue())))
    } else {
        return(returnValue$getReturnValue())
    }
}