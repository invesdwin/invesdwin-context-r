print("getPercent")
if(exists("getPercent")){
	stop("getPercent already defined!")
}
getPercent <- callback("getPercent")
print(typeof(getPercent))
print(getPercent)
if(typeof(getPercent) != "double"){
	stop("getPercent not double!")
}
callback("putPercent",getPercent)

print("getPercentVector")
if(exists("getPercentVector")){
	stop("getPercentVector already defined!")
}
getPercentVector <- callback("getPercentVector")
print(typeof(getPercentVector))
print(getPercentVector)
if(typeof(getPercentVector) != "double"){
	stop("getPercentVector not double!")
}
callback("putPercentVector",getPercentVector)

print("getPercentVectorAsList")
if(exists("getPercentVectorAsList")){
	stop("getPercentVectorAsList already defined!")
}
getPercentVectorAsList <- callback("getPercentVectorAsList")
print(typeof(getPercentVectorAsList))
print(getPercentVectorAsList)
if(typeof(getPercentVectorAsList) != "double"){
	stop("getPercentVectorAsList not double!")
}
callback("putPercentVectorAsList",getPercentVectorAsList)

print("getPercentMatrix")
if(exists("getPercentMatrix")){
	stop("getPercentMatrix already defined!")
}
getPercentMatrix <- callback("getPercentMatrix")
print(typeof(getPercentMatrix))
print(getPercentMatrix)
if(typeof(getPercentMatrix) != "double"){
	stop("getPercentMatrix not double!")
}
callback("putPercentMatrix",getPercentMatrix)

print("getPercentMatrixAsList")
if(exists("getPercentMatrixAsList")){
	stop("getPercentMatrixAsList already defined!")
}
getPercentMatrixAsList <- callback("getPercentMatrixAsList")
print(typeof(getPercentMatrixAsList))
print(getPercentMatrixAsList)
if(typeof(getPercentMatrixAsList) != "double"){
	stop("getPercentMatrixAsList not double!")
}
callback("putPercentMatrixAsList",getPercentMatrixAsList)
