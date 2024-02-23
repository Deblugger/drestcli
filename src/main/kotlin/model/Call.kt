package deblugger.me.model

import deblugger.me.resolvePlaceHolders

data class Call(
	val method: MethodEnum,
	var url: String,
	var headers: List<CallHeader>,
	var body: String
)

data class CallHeader(
	val key: String,
	val value: String
)

fun Call.replaceVariables(envVariables: List<EnvVariables>) = Call(
	method = this.method,
	url = this.url.resolvePlaceHolders(envVariables),
	body = this.body.resolvePlaceHolders(envVariables),
	headers = this.headers.map {
		CallHeader(it.key, it.value.resolvePlaceHolders(envVariables))
	}
)