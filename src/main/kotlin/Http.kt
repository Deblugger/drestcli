package deblugger.me

import deblugger.me.model.Call
import deblugger.me.model.MethodEnum
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*
import kotlin.system.exitProcess

private val client = OkHttpClient()

fun doTheCall(call: Call) {
	val headers = extractHeaders(call)

	when(call.method) {
		MethodEnum.POST -> doPost(call.url, headers, call.body)
		MethodEnum.GET -> doGet(call.url, headers)
		MethodEnum.PUT -> doPut(call.url, headers, call.body)
		else -> {
			println("Request method not allowed")
			exitProcess(-1)
		}
	}
}

fun getOauth2(args: List<String>) {
	val authConfig = getAuthConfig(args)

	val base64Authorization = Base64.getEncoder().encodeToString("${authConfig.clientId}:${authConfig.clientSecret}"
																	 .toByteArray())

	val headers = Headers.Builder()
		.add("Authorization", "Basic $base64Authorization")
		.add("Content-Type", "application/x-www-form-urlencoded")
		.build()

	val requestBody = FormBody.Builder()
		.addEncoded("grant_type", authConfig.grantType)
		.build()


	val request = Request.Builder()
		.url(authConfig.tokenUrl)
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

private fun extractHeaders(call: Call): Headers {
	val headersBuilder = Headers.Builder()
	call.headers.forEach {
		headersBuilder.add(it.key, it.value)
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