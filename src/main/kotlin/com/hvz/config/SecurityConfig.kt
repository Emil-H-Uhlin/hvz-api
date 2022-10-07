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
            //region game
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games").hasAuthority("read:games")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/{*}").hasAuthority("read:games")
            .mvcMatchers(HttpMethod.POST, "$BASE_API_PATH/games").hasAuthority("create:games")
            .mvcMatchers(HttpMethod.DELETE, "$BASE_API_PATH/games/{*}").hasAuthority("delete:games")
            .mvcMatchers(HttpMethod.PUT, "$BASE_API_PATH/games/{*}").hasAuthority("update:games")
            //endregion
            //region players
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/players").hasAuthority("ADMIN_read:players")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/players/{*}").hasAuthority("ADMIN_read:players")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/{*}/players").hasAuthority("read:players")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/{*}/players/{*}").hasAuthority("read:players")
            .mvcMatchers(HttpMethod.PUT, "$BASE_API_PATH/players/{*}").hasAuthority("ADMIN_update:players")
            .mvcMatchers(HttpMethod.DELETE, "$BASE_API_PATH/players/{*}").hasAuthority("delete:players")
            .mvcMatchers(HttpMethod.POST, "$BASE_API_PATH/games/{*}/players").hasAuthority("create:players")
            //endregion
            //region missions
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/missions").hasAuthority("ADMIN_read:missions")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/missions/{*}").hasAuthority("ADMIN_read:missions")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/{*}/missions").hasAuthority("read:missions")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/{*}/missions/{*}").hasAuthority("read:missions")
            .mvcMatchers(HttpMethod.PUT, "$BASE_API_PATH/missions/{*}").hasAuthority("update:missions")
            .mvcMatchers(HttpMethod.DELETE, "$BASE_API_PATH/missions/{*}").hasAuthority("delete:missions")
            .mvcMatchers(HttpMethod.POST, "$BASE_API_PATH/games/{*}/missions").hasAuthority("create:missions")
            //endregion
            //region kills
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/kills").hasAuthority("ADMIN_read:kills")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/kills/{*}").hasAuthority("ADMIN_read:kills")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/{*}/kills").hasAuthority("read:kills")
            .mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/{*}/kills/{*}").hasAuthority("read:kills")
            .mvcMatchers(HttpMethod.PUT, "$BASE_API_PATH/kills/{*}").hasAuthority("update:kills")
            .mvcMatchers(HttpMethod.DELETE, "$BASE_API_PATH/kills/{*}").hasAuthority("delete:kills")
            .mvcMatchers(HttpMethod.POST, "$BASE_API_PATH/games/{*}/kills").hasAuthority("create:kills")
            //endregion

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