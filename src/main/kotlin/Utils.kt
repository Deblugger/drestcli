package deblugger.me

import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import kotlin.system.exitProcess

fun getConfig(args: List<String>, type: String): JSONObject? {
	return if (args.contains("--env")) {
		val call = args[args.indexOf("--env") + 1]

		try {
			val file = File("/home/${System.getProperty("user.name")}/.drestcli/$type/$call.json")
			JSONObject(file.inputStream().readBytes().toString(Charset.defaultCharset()))
		} catch (ex: Exception) {
			println("Exception thrown trying to read call!")
			println(ex)
			exitProcess(-1)
		}
	} else {
		null
	}
}