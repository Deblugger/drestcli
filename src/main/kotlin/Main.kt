package deblugger.me

import com.fasterxml.jackson.module.kotlin.readValue
import deblugger.me.model.*
import deblugger.me.model.Call
import okhttp3.*
import java.io.File
import java.nio.charset.Charset
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
	val envConfig = getEnvVariables(args)
	var call = getCall(args)
	if (envConfig != null) {
		call = call.replaceVariables(envConfig)
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

private fun getCall(args: List<String>): Call {
	val call = args[args.indexOf("--call") + 1]

	return try {
		val file = File("/home/${System.getProperty("user.name")}/.drestcli/collection/$call.json")
		objectMapper.readValue(file)
	} catch (ex: Exception) {
		println("Exception thrown trying to read env configuration!")
		println(ex)
		exitProcess(-1)
	}
}
