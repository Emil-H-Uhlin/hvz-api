package com.hvz.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig {
    @Value("\${spring.security.oauth2.resourceserver.jwt.audiences}")
    final lateinit var audience: String

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    final lateinit var issuer: String

    companion object {
        const val BASE_API_PATH = "/api/v1"
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = with(http) {
        cors()
            .and().sessionManagement().disable()
            .csrf().disable()
            .oauth2ResourceServer {
                it.jwt().jwtAuthenticationConverter(jwtPermissionsConverter())
            }
            .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "$BASE_API_PATH/games").hasAuthority("create:games")
                .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games").hasAuthority("read:games")
                .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/{*}").hasAuthority("read:games")


        build()
    }

    @Bean
    fun jwtDecoder(): NimbusJwtDecoder = JwtDecoders.fromOidcIssuerLocation<NimbusJwtDecoder>(issuer).apply {
        val audienceValidator = AudienceValidator(audience)
        val withIssuer = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)

        setJwtValidator(withAudience)
    }

    fun jwtPermissionsConverter(): JwtAuthenticationConverter {
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