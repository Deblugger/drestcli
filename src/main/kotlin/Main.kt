package deblugger.me

import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.util.Base64
import kotlin.system.exitProcess

fun main(args: Array<String>) {
	processArguments(args.asList())
}

private fun processArguments(args: List<String>) {
	when {
		args.contains("--help") -> displayHelp()
		args.contains("--call") -> buildCall(args)
		args.contains("--oauth2") -> getOauth2(args)
		else -> {
			println("Unrecognized arguments, use --help")
			exitProcess(-1)
		}
	}
}

private fun buildCall(args: List<String>) {
	val envConfig = getConfig(args, "configuration")
	var call = getCall(args)
	if (envConfig != null) {
		call = replaceVariables(call, envConfig)
	}

	doTheCall(call)
}

private fun displayHelp() {
	println("""
		This is a REST Client via CLI, to configure it you can go to ~/.drestcli/configuration and create environment in json
		To add new calls you can go to ~/.drestcli/collections, under that folder you can create your own
		
		To retrieve oauth2 token use: drestcli --env <env> --oauth2
		
		Basic usage: drestcli --env <env> --call <collection>/<call>		
	""".trimIndent())
}

private fun getCall(args: List<String>): JSONObject {
	val call = args[args.indexOf("--call") + 1]

	return try {
		val file = File("/home/${System.getProperty("user.name")}/.drestcli/collection/$call.json")
		JSONObject(file.inputStream().readBytes().toString(Charset.defaultCharset()))
	} catch (ex: Exception) {
		println("Exception thrown trying to read env configuration!")
		println(ex)
		exitProcess(-1)
	}
}

private fun replaceVariables(call: JSONObject, envConfig: JSONObject): JSONObject {
	call.keys().forEach {
		var jsonValue = call[it].toString()
		val startOcurrences = jsonValue.getAllIndexOf("\${")
		val endOcurrences = jsonValue.getAllIndexOf("}$")


		var mergedOcurrences = startOcurrences.plus(endOcurrences).sorted()
		if (mergedOcurrences.size % 2 > 0) {
			println("Failure trying to parse variables, have you forget some \"}$\"?")
			exitProcess(-1)
		}

		while (mergedOcurrences.isNotEmpty()) {
			val variable = jsonValue.substring(mergedOcurrences[0], mergedOcurrences[1] + 2)
			jsonValue = jsonValue.replace(variable, envConfig[variable.trimVariable()].toString())
			mergedOcurrences = mergedOcurrences.drop(2) // drop the first two
		}
		call.put(it, jsonValue)
	}

	return call
}