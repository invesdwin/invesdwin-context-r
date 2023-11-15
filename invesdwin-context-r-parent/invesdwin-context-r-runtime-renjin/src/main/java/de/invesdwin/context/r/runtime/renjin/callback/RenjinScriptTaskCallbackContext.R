if (!require("jsonlite")) {
  library("jsonlite")
}

import(de.invesdwin.context.r.runtime.renjin.callback.RenjinScriptTaskCallbackContext)
callback <- function(methodName, ...) {
    if(!exists("renjinScriptTaskCallbackContext")) {
        if(exists("renjinScriptTaskCallbackContextUuid")) {
            .GlobalEnv$renjinScriptTaskCallbackContext <- RenjinScriptTaskCallbackContext$getContext(renjinScriptTaskCallbackContextUuid)
        } else {
            stop("IScriptTaskCallback not available")
        }
    }
    parameters <- c(...)
    dims <- sapply(parameters, dim)
    returnValue = renjinScriptTaskCallbackContext$invoke(methodName, toJSON(dims), toJSON(parameters))
	return(eval(parse(text=returnValue)))
}