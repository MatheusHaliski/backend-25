package com.example.projetandosist

import com.example.projetandosist.security.JwtFilter
import com.example.projetandosist.security.JwtUtil
import com.example.projetandosist.PessoaRepository

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

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
                    .requestMatchers(
                        "/auth/**",
                        "/pessoas",
                        "/pessoas/**",
                        "/produtos/**",
                        "/doacoes/**",
                        "/login.html",
                        "/perfil",
                        "/relatorio/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/h2-console/**"
                    ).permitAll()
                    .requestMatchers("/admin/**").hasAuthority("USUARIO_ADM")
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
