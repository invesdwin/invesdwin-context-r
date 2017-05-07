print("getString")
getString <- putString
print(typeof(getString))
print(getString)
if(typeof(getString) != "character"){
	stop("getString not character!")
}

print("getStringNull")
getStringNull <- putStringNull
print(typeof(getStringNull))
print(getStringNull)
if(typeof(getStringNull) != "character"){
	stop("getStringNull not character!")
}
if(!is.na(getStringNull)){
	stop("getStringNull not na!")
}

print("getStringVector")
getStringVector <- putStringVector
print(typeof(getStringVector))
print(getStringVector)
if(typeof(getStringVector) != "character"){
	stop("getStringVector not character!")
}


print("getStringVectorNull")
getStringVectorNull <- putStringVectorNull
print(typeof(getStringVectorNull))
print(getStringVectorNull)
if(typeof(getStringVectorNull) != "character"){
	stop("getStringVectorNull not character!")
}
if(!is.na(getStringVectorNull[2])){
	stop("getStringVectorNull[2] not na!")
}

print("getStringVectorAsList")
getStringVectorAsList <- putStringVectorAsList
print(typeof(getStringVectorAsList))
print(getStringVectorAsList)
if(typeof(getStringVectorAsList) != "character"){
	stop("getStringVectorAsList not character!")
}

print("getStringVectorAsListNull")
getStringVectorAsListNull <- putStringVectorAsListNull
print(typeof(getStringVectorAsListNull))
print(getStringVectorAsListNull)
if(typeof(getStringVectorAsListNull) != "character"){
	stop("getStringVectorAsListNull not character!")
}
if(!is.na(getStringVectorAsListNull[2])){
	stop("getStringVectorAsListNull[2] not na!")
}