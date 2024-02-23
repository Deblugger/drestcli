package deblugger.me.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthConfig(
	@JsonProperty("grant_type")
	val grantType: String,
	@JsonProperty("token_url")
	val tokenUrl: String,
	@JsonProperty("client_id")
	val clientId: String,
	@JsonProperty("client_secret")
	val clientSecret: String
)
