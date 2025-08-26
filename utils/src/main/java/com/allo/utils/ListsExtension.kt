package com.allo.utils




fun <E> List<E>.partition(singleSize: Int): List<List<E>> {
    val result = mutableListOf<List<E>>()
    if (isEmpty()){
        return result
    }

    if (singleSize <= 0){
        return result
    }

    if (singleSize >= size){
        result.add(this)
        return result
    }

    val splitNum = if (size % singleSize == 0) size / singleSize else size / singleSize + 1

    var value :List<E>

    for(i in 0 until splitNum){
        value = if (i < splitNum - 1 ){
            subList(i*singleSize,(i+1)*singleSize)
        }else{
            subList(i*singleSize,size)
        }
        result.add(value)
    }



    return result
}