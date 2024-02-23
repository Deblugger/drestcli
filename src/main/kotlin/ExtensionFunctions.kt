package deblugger.me


fun String.getAllIndexOf(search: String): MutableList<Int> {
	val result = mutableListOf<Int>()
	var index = this.indexOf(search)
	while (index > -1) {
		result.add(index)
		index = this.indexOf(search, index + 1)
	}

	return result
}

fun String.trimVariable() = this.replace("\${", "").replace("}$", "")

fun String.toColor(color: Color) = "${color.foreground}$this${RESET}"