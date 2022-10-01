package com.hvz.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
class SecurityConfig {
    @Value("\${spring.security.oauth2.resourceserver.jwt.audiences}")
    lateinit var audience: String

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    lateinit var issuer: String

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = with(http) {
        cors()
            .and().sessionManagement().disable()
            .csrf().disable()
            .authorizeRequests {
                it.mvcMatchers(HttpMethod.POST, "/api/v1/games").hasAuthority("games:create")
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtRoleAuthenticationConverter())

        build()
    }

    @Bean
    fun jwtDecoder(): NimbusJwtDecoder = JwtDecoders.fromOidcIssuerLocation<NimbusJwtDecoder>(issuer).apply {
        val audienceValidator = AudienceValidator(audience)
        val withIssuer = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)

        setJwtValidator(withAudience)
    }

    @Bean
    fun jwtRoleAuthenticationConverter(): JwtAuthenticationConverter {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter().apply {
            setAuthoritiesClaimName("permissions")
            setAuthorityPrefix("")
        }

        return JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        }
    }

    private class AudienceValidator(private val audience: String): OAuth2TokenValidator<Jwt> {

        override fun validate(token: Jwt?): OAuth2TokenValidatorResult {
            val err = OAuth2Error("invalid_token", "The required audience is missing", null)

            return if (token?.audience!!.contains(audience))
                OAuth2TokenValidatorResult.success()
            else OAuth2TokenValidatorResult.failure(err)
        }
    }
}