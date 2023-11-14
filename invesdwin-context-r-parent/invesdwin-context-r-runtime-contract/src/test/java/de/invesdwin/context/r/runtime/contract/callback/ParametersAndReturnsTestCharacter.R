print("getCharacter")
if(exists("getCharacter")){
	stop("getCharacter already defined!")
}
getCharacter <- callback("getCharacter")
print(typeof(getCharacter))
print(getCharacter)
if(typeof(getCharacter) != "character"){
	stop("getCharacter not character!")
}
callback("putCharacter",getCharacter)

print("getCharacterVector")
if(exists("getCharacterVector")){
	stop("getCharacterVector already defined!")
}
getCharacterVector <- callback("getCharacterVector")
print(typeof(getCharacterVector))
print(getCharacterVector)
if(typeof(getCharacterVector) != "character"){
	stop("getCharacterVector not character!")
}
callback("putCharacterVector",getCharacterVector)

print("getCharacterVectorAsList")
if(exists("getCharacterVectorAsList")){
	stop("getCharacterVectorAsList already defined!")
}
getCharacterVectorAsList <- callback("getCharacterVectorAsList")
print(typeof(getCharacterVectorAsList))
print(getCharacterVectorAsList)
if(typeof(getCharacterVectorAsList) != "character"){
	stop("getCharacterVectorAsList not character!")
}
callback("putCharacterVectorAsList",getCharacterVectorAsList)

print("getCharacterMatrix")
if(exists("getCharacterMatrix")){
	stop("getCharacterMatrix already defined!")
}
getCharacterMatrix <- callback("getCharacterMatrix")
print(typeof(getCharacterMatrix))
print(getCharacterMatrix)
if(typeof(getCharacterMatrix) != "character"){
	stop("getCharacterMatrix not character!")
}
callback("putCharacterMatrix",getCharacterMatrix)

print("getCharacterMatrixAsList")
if(exists("getCharacterMatrixAsList")){
	stop("getCharacterMatrixAsList already defined!")
}
getCharacterMatrixAsList <- callback("getCharacterMatrixAsList")
print(typeof(getCharacterMatrixAsList))
print(getCharacterMatrixAsList)
if(typeof(getCharacterMatrixAsList) != "character"){
	stop("getCharacterMatrixAsList not character!")
}
callback("putCharacterMatrixAsList",getCharacterMatrixAsList)
