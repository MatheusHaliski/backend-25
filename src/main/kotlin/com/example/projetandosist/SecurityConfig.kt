package com.example.projetandosist

import com.example.projetandosist.security.JwtFilter
import com.example.projetandosist.security.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*

@Configuration
class SecurityConfig(
    private val jwtUtil: JwtUtil,
    private val pessoaRepository: PessoaRepository
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { }
            .headers { it.frameOptions { frame -> frame.sameOrigin() } }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(AntPathRequestMatcher("/auth/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/pessoas/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/produtos/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/doacoes/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/login.html")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/perfil")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/relatorio/**")).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher("/swagger-ui/**"),
                        AntPathRequestMatcher("/v3/api-docs/**")
                    ).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/h2-console/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/admin/**")).hasAuthority("USUARIO_ADM")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtFilter(jwtUtil, pessoaRepository),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("https://front-end25v8.onrender.com")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
