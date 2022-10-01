fun main() {
    println(Calculator(readLine()))
}

fun Calculator(str: String?) : String {
    // code goes here
    if (str == null) return ""

    var s = str
    s = s.replace("\\s".toRegex(), "")
    s = s.replace("[)][(]".toRegex(), ")*(")
    s = s.replace("([0-9])[(]".toRegex(), "$1*(")
    s = s.replace("[)]([0-9])".toRegex(), ")*$1")

    var operations: List<String> = parseOperations(s)
    operations = evalBraces(operations)
    operations = evalMultiplyDivide(operations)
    operations = evalAddSubtract(operations)

    return operations[0]
}

fun hasNext(i: Int, spl: List<String>): Boolean {
    return i + 1 < spl.size
}

fun indexOfOperator(x: Int, i: Int, spl: List<String>): Int {
    return if (i == 0) x else if (hasNext(i, spl)) x + 1 else -1
}

fun operatorSymbol(i: Int, spl: List<String>, str: String, x: Int): String {
    return if (hasNext(i, spl)) str[x].toString() else ""
}

fun parseOperations(str: String): List<String> {
    val operations: MutableList<String> = ArrayList()
    val spl = str.split("[*/+-]".toRegex())
    var operatorIndex = 0
    for (i in spl.indices) {
        var s = spl[i]
        operatorIndex += spl[i].length
        operatorIndex = indexOfOperator(operatorIndex, i, spl)
        val operator: String = operatorSymbol(i, spl, str, operatorIndex)
        if (s[0] == '(') {
            s += operator
            operations.add(s)
        } else if (s[s.length - 1] == ')') {
            s = operations[operations.size - 1] + s
            operations[operations.size - 1] = s
            if (hasNext(i, spl)) {
                operations.add(operator)
            }
        } else {
            operations.add(s)
            if (hasNext(i, spl)) {
                operations.add(operator)
            }
        }
    }
    return operations
}

fun evalBraces(src: List<String>): List<String> {
    val tmpList: MutableList<String> = java.util.ArrayList(src)
    for (i in src.indices) {
        val v = src[i]
        if (v[0] == '(') {
            val s = v.substring(1, v.length - 1)
            var tmps: List<String> = parseOperations(s)
            tmps = evalMultiplyDivide(tmps)
            tmps = evalAddSubtract(tmps)
            tmpList[i] = tmps[0]
        }
    }
    return tmpList
}

fun evalMultiplyDivide(src: List<String>): List<String> {
    val tmpList: MutableList<String> = java.util.ArrayList(src)
    for (i in src.indices) {
        val v = src[i]
        if (v.matches("[*/]".toRegex())) {
            val prevIndex = i - 1
            val nextIndex = i + 1
            val n1 = src[prevIndex].toInt()
            val n2 = src[nextIndex].toInt()
            val nx = if (v == "*") n1 * n2 else n1 / n2
            tmpList[i] = nx.toString()
            tmpList.removeAt(nextIndex)
            tmpList.removeAt(prevIndex)
            return evalMultiplyDivide(tmpList)
        }
    }

    return tmpList
}

fun evalAddSubtract(src: List<String>): List<String> {
    val tmpList: MutableList<String> = java.util.ArrayList(src)
    for (i in src.indices) {
        val v = src[i]
        if (v == "+" || v == "-") {
            val prevIndex = i - 1
            val nextIndex = i + 1
            val n1 = src[prevIndex].toInt()
            val n2 = src[nextIndex].toInt()
            val nx = if (v == "+") n1 + n2 else n1 - n2
            tmpList[i] = nx.toString()
            tmpList.removeAt(nextIndex)
            tmpList.removeAt(prevIndex)
            return evalAddSubtract(tmpList)
        }
    }

    return tmpList
}