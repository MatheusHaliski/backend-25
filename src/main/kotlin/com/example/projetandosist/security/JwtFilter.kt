package com.example.projetandosist.security

import com.example.projetandosist.PessoaRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val pessoaRepository: PessoaRepository
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/auth") ||
               path.startsWith("/pessoas") || 
               path.startsWith("/produtos") ||
               path.startsWith("/doacoes") ||
               path.startsWith("/relatorio") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/h2-console")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7)

            try {
                if (jwtUtil.validateToken(token)) {
                    val email = jwtUtil.getEmail(token)
                    val pessoa = pessoaRepository.findByEmail(email)

                    if (pessoa != null) {
                        val authorities = listOf(SimpleGrantedAuthority(pessoa.tipoDeUsuario.toString()))
                        val authentication = UsernamePasswordAuthenticationToken(
                            pessoa,
                            null,
                            authorities
                        )
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                }
            } catch (ex: Exception) {
                println("Erro na validação do token: ${ex.message}")
            }
        }

        filterChain.doFilter(request, response)
    }
}
