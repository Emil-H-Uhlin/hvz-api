package com.hvz.config

import com.hvz.config.auth0.AudienceValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
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
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtRoleAuthenticationConverter())

        build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder = JwtDecoders.fromOidcIssuerLocation<NimbusJwtDecoder>(issuer).apply {
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
}