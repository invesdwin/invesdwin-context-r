if (!require("jsonlite")) {
  install.packages('jsonlite', dependencies=TRUE, repos='http://cran.rstudio.com')
  library("jsonlite")
}

callback_createSocket <- function(){
	.GlobalEnv$socketScriptTaskCallbackSocket <- socketConnection(host=socketScriptTaskCallbackServerHost, port = socketScriptTaskCallbackServerPort, blocking=TRUE, server=FALSE, open="r+")
	writeLines(socketScriptTaskCallbackContextUuid, socketScriptTaskCallbackSocket)
}

callback_invokeSocket <- function(methodName, parameters){
	dims <- sapply(parameters, dim)
    writeLines(paste(methodName, ";", toJSON(dims), ";", toJSON(parameters), sep=""), socketScriptTaskCallbackSocket)
    returnExpression <- readLines(socketScriptTaskCallbackSocket, 1L)
    return(eval(parse(text=returnExpression)))
}

callback <- function(methodName, parameters = list()){
    if(!exists("socketScriptTaskCallbackContext")){
        if(exists("socketScriptTaskCallbackContextUuid")){
            callback_createSocket()
        } else {
            stop("IScriptTaskCallback not available")
        }
    }
    return(callback_invokeSocket(methodName, parameters))
}
