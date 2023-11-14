print("getBoolean")
if(exists("getBoolean")){
	stop("getBoolean already defined!")
}
getBoolean <- callback("getBoolean")
print(typeof(getBoolean))
print(getBoolean)
if(typeof(getBoolean) != "logical"){
	stop("getBoolean not logical!")
}
callback("putBoolean",getBoolean)

print("getBooleanVector")
if(exists("getBooleanVector")){
	stop("getBooleanVector already defined!")
}
getBooleanVector <- callback("getBooleanVector")
print(typeof(getBooleanVector))
print(getBooleanVector)
if(typeof(getBooleanVector) != "logical"){
	stop("getBooleanVector not logical!")
}
callback("putBooleanVector",getBooleanVector)

print("getBooleanVectorAsList")
if(exists("getBooleanVectorAsList")){
	stop("getBooleanVectorAsList already defined!")
}
getBooleanVectorAsList <- callback("getBooleanVectorAsList")
print(typeof(getBooleanVectorAsList))
print(getBooleanVectorAsList)
if(typeof(getBooleanVectorAsList) != "logical"){
	stop("getBooleanVectorAsList not logical!")
}
callback("putBooleanVectorAsList",getBooleanVectorAsList)

print("getBooleanMatrix")
if(exists("getBooleanMatrix")){
	stop("getBooleanMatrix already defined!")
}
getBooleanMatrix <- callback("getBooleanMatrix")
print(typeof(getBooleanMatrix))
print(getBooleanMatrix)
if(typeof(getBooleanMatrix) != "logical"){
	stop("getBooleanMatrix not logical!")
}
callback("putBooleanMatrix",getBooleanMatrix)

print("getBooleanMatrixAsList")
if(exists("getBooleanMatrixAsList")){
	stop("getBooleanMatrixAsList already defined!")
}
getBooleanMatrixAsList <- callback("getBooleanMatrixAsList")
print(typeof(getBooleanMatrixAsList))
print(getBooleanMatrixAsList)
if(typeof(getBooleanMatrixAsList) != "logical"){
	stop("getBooleanMatrixAsList not logical!")
}
callback("putBooleanMatrixAsList",getBooleanMatrixAsList)
