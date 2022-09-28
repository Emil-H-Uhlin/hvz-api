package com.hvz.auth0

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt

class AudienceValidator(private val audience: String): OAuth2TokenValidator<Jwt> {

    override fun validate(token: Jwt?): OAuth2TokenValidatorResult {
        val err = OAuth2Error("invalid_token", "The required audience is missing", null)

        return if (token == null || !token.audience.contains(audience))
            OAuth2TokenValidatorResult.failure(err)
        else OAuth2TokenValidatorResult.success()
    }

}