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
    final lateinit var audience: String

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    final lateinit var issuer: String

    companion object {
        const val BASE_API_PATH = "/api/v1/"
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = with(http) {
        cors()
            .and().sessionManagement().disable()
            .csrf().disable()
            .authorizeRequests { authorize -> with(authorize) {
                mvcMatchers("/swagger-ui.html",
                    "/v3/api-docs",
                    "/swagger-ui/index.html",
                    "/swagger-ui/index.html/**/*",
                    "/swagger-ui/index-html#/**/*"
                ).permitAll()

                //region Games
                mvcMatchers(HttpMethod.POST, "$BASE_API_PATH/games")
                    .hasAuthority("create:games")

                mvcMatchers(HttpMethod.GET,"$BASE_API_PATH/games",
                    "$BASE_API_PATH/games/*")
                    .hasAuthority("read:games")

                mvcMatchers(HttpMethod.PUT, "$BASE_API_PATH/games/*")
                    .hasAuthority("update:games")

                mvcMatchers(HttpMethod.DELETE, "$BASE_API_PATH/games/*")
                    .hasAuthority("delete:games")
                //endregion
                //region Players
                mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/players",
                    "$BASE_API_PATH/players/*"
                ).hasAuthority("ADMIN_read:players")

                mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/*/players"
                    , "$BASE_API_PATH/games/*/players/*"
                ).hasAuthority("read:players")

                mvcMatchers(HttpMethod.POST, "$BASE_API_PATH/games/*/players")
                    .hasAuthority("create:players")

                mvcMatchers(HttpMethod.PUT, "$BASE_API_PATH/games/*/players")
                    .hasAuthority("update:players")

                mvcMatchers(HttpMethod.DELETE, "$BASE_API_PATH/players/*",
                    "$BASE_API_PATH/games/*/players/*"
                ).hasAuthority("delete:players")
                //endregion
                //region Kills
                mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/kills",
                    "$BASE_API_PATH/kills/*"
                ).hasAuthority("ADMIN_read:kills")

                mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/*/kills",
                    "$BASE_API_PATH/games/*/kills/*"
                ).hasAuthority("read:kills")

                mvcMatchers(HttpMethod.PUT, "$BASE_API_PATH/kills/*",
                    "$BASE_API_PATH/games/*/kills/*"
                ).hasAuthority("update:kills")

                mvcMatchers(HttpMethod.POST, "$BASE_API_PATH/games/*/kills")
                    .hasAuthority("create:kills")
                //endregion
                //region Missions
                mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/missions",
                    "$BASE_API_PATH/missions/*"
                ).hasAuthority("ADMIN_read:missions")

                mvcMatchers(HttpMethod.GET, "$BASE_API_PATH/games/*/missions",
                    "$BASE_API_PATH/games/*/missions/*"
                ).hasAuthority("read:missions")

                mvcMatchers(HttpMethod.POST, "$BASE_API_PATH/games/*/missions")
                    .hasAuthority("create:missions")

                mvcMatchers(HttpMethod.PUT, "$BASE_API_PATH/games/*/missions/*",
                    "$BASE_API_PATH/missions/*"
                ).hasAuthority("update:missions")
                //endregion
            }}
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtPermissionsConverter())

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