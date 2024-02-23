package deblugger.me

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import deblugger.me.model.AuthConfig
import deblugger.me.model.EnvVariables
import java.io.File
import java.nio.charset.Charset
import kotlin.system.exitProcess

val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

fun getEnvVariables(args: List<String>): List<EnvVariables>? {
	return if (args.contains("--env")) {
		val call = args[args.indexOf("--env") + 1]

		try {
			val file = File("/home/${System.getProperty("user.name")}/.drestcli/configuration/$call.json")
			objectMapper.readValue(file)
		} catch (ex: Exception) {
			println("Exception thrown trying to read call!")
			println(ex)
			exitProcess(-1)
		}
	} else {
		null
	}
}

fun getAuthConfig(args: List<String>): AuthConfig {
	return if (args.contains("--env")) {
		val call = args[args.indexOf("--env") + 1]

		try {
			val file = File("/home/${System.getProperty("user.name")}/.drestcli/authentication/$call.json")
			objectMapper.readValue(file.inputStream().readBytes().toString(Charset.defaultCharset()), AuthConfig::class.java)
		} catch (ex: Exception) {
			println("Exception thrown trying to read call!")
			println(ex)
			exitProcess(-1)
		}
	} else {
		println("--oauth2 option should have --env also included")
		exitProcess(-1)
	}
}


fun String.resolvePlaceHolders(envVariables: List<EnvVariables>): String {
	val startOcurrences = this.getAllIndexOf("\${")
	val endOcurrences = this.getAllIndexOf("}$")


	var mergedOcurrences = startOcurrences.plus(endOcurrences).sorted()
	if (mergedOcurrences.size % 2 > 0) {
		println("Failure trying to parse variables, have you forget some \"}$\"?")
		exitProcess(-1)
	}
	var newValue = this
	while (mergedOcurrences.isNotEmpty()) {
		val variable = this.substring(mergedOcurrences[0], mergedOcurrences[1] + 2)
		newValue = newValue.replace(variable, envVariables.searchValue(variable.trimVariable()))
		mergedOcurrences = mergedOcurrences.drop(2) // drop the first two
	}
	return newValue
}

fun List<EnvVariables>.searchValue(key: String) = this.first { it.key == key }.value