print("getDouble")
if(exists("getDouble")){
	stop("getDouble already defined!")
}
getDouble <- callback("getDouble")
print(typeof(getDouble))
print(getDouble)
if(typeof(getDouble) != "double"){
	stop("getDouble not double!")
}
callback("putDouble",getDouble)

print("getDoubleVector")
if(exists("getDoubleVector")){
	stop("getDoubleVector already defined!")
}
getDoubleVector <- callback("getDoubleVector")
print(typeof(getDoubleVector))
print(getDoubleVector)
if(typeof(getDoubleVector) != "double"){
	stop("getDoubleVector not double!")
}
callback("putDoubleVector",getDoubleVector)

print("getDoubleVectorAsList")
if(exists("getDoubleVectorAsList")){
	stop("getDoubleVectorAsList already defined!")
}
getDoubleVectorAsList <- callback("getDoubleVectorAsList")
print(typeof(getDoubleVectorAsList))
print(getDoubleVectorAsList)
if(typeof(getDoubleVectorAsList) != "double"){
	stop("getDoubleVectorAsList not double!")
}
callback("putDoubleVectorAsList",getDoubleVectorAsList)

print("getDoubleMatrix")
if(exists("getDoubleMatrix")){
	stop("getDoubleMatrix already defined!")
}
getDoubleMatrix <- callback("getDoubleMatrix")
print(typeof(getDoubleMatrix))
print(getDoubleMatrix)
if(typeof(getDoubleMatrix) != "double"){
	stop("getDoubleMatrix not double!")
}
callback("putDoubleMatrix",getDoubleMatrix)

print("getDoubleMatrixAsList")
if(exists("getDoubleMatrixAsList")){
	stop("getDoubleMatrixAsList already defined!")
}
getDoubleMatrixAsList <- callback("getDoubleMatrixAsList")
print(typeof(getDoubleMatrixAsList))
print(getDoubleMatrixAsList)
if(typeof(getDoubleMatrixAsList) != "double"){
	stop("getDoubleMatrixAsList not double!")
}
callback("putDoubleMatrixAsList",getDoubleMatrixAsList)
