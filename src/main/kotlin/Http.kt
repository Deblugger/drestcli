package deblugger.me

import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.system.exitProcess

private val client = OkHttpClient()

fun doTheCall(call: JSONObject) {
	val headers = extractHeaders(call)

	when(call["method"]) {
		"POST" -> doPost(call["url"].toString(), headers, call["body"].toString())
		"GET" -> doGet(call["url"].toString(), headers)
		"PUT" -> doPut(call["url"].toString(), headers, call["body"].toString())
		else -> {
			println("Request method not allowed")
			exitProcess(-1)
		}
	}
}

fun getOauth2(args: List<String>) {
	val envConfig = getConfig(args, "authentication")

	val base64Authorization = Base64.getEncoder().encodeToString("${envConfig!!.getString("client_id")}:${envConfig.getString("client_secret")}"
																	 .toByteArray())

	val headers = Headers.Builder()
		.add("Authorization", "Basic $base64Authorization")
		.add("Content-Type", "application/x-www-form-urlencoded")
		.build()

	val requestBody = FormBody.Builder()
		.addEncoded("grant_type", envConfig.getString("grant_type"))
		.build()


	val request = Request.Builder()
		.url(envConfig.getString("token_url"))
		.post(requestBody)
		.headers(headers)
		.build()

	performCall(request)
}

private fun doPost(url: String, headers: Headers, body: String) {
	val requestBody = if (headers["Content-Type"] == "application/zip") {
		MultipartBody.Builder().addFormDataPart("file", null, File(body).asRequestBody()).build()
	} else {
		body.toRequestBody()
	}

	val request = Request.Builder()
		.url(url)
		.post(requestBody)
		.headers(headers)
		.build()

	performCall(request)
}

private fun doGet(url: String, headers: Headers) {
	val request = Request.Builder()
		.url(url)
		.headers(headers)
		.get()
		.build()

	performCall(request)
}

private fun doPut(url: String, headers: Headers, body: String) {
	val request = Request.Builder()
		.url(url)
		.post(body.toRequestBody())
		.headers(headers)
		.build()

	performCall(request)
}


private fun extractHeaders(call: JSONObject): Headers {
	var headersBuilder = Headers.Builder()
	val jsonHeaders = JSONArray(call["headers"].toString())
	jsonHeaders.toList().forEach {
		it as Map<String, String>
		headersBuilder.add(it["key"]!!, it["value"]!!)
	}
	return headersBuilder.build()
}

private fun performCall(request: Request) {
	val response = client.newCall(request).execute()

	val statusColor = when (response.code) {
		in 200 .. 299 -> Color.GREEN
		in 300 .. 499 -> Color.YELLOW
		else -> Color.RED
	}

	println("\n\nStatus Code: ${response.code.toString().toColor(statusColor)}")
	println("\n\nResponse headers:")
	response.headers.forEach { println("${it.first} = ${it.second}") }
	println("\n\nResponse body:")
	println(response.body?.string()?.toColor(Color.GREEN) ?: "<empty>".toColor(Color.RED))
}