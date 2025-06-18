package com.example.projetandosist

import com.example.projetandosist.security.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.SimpleMailMessage
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = ["https://front-end25v8.onrender.com"])
class AuthController(
    private val pessoaRepository: PessoaRepository,
    private val jwtUtil: JwtUtil,
    private val emailService: EmailService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        val pessoa = pessoaRepository.findByEmailAndSenha(
            loginRequest.email,
            loginRequest.senha
        ) ?: return ResponseEntity.status(401).body("Credenciais inválidas")

        val token = jwtUtil.generateToken(pessoa.email, pessoa.tipoDeUsuario.toString())
        return ResponseEntity.ok(mapOf("token" to token))
    }
    @PostMapping("/forgetpassword")
    fun enviarEmailRedefinicao(@RequestParam email: String): ResponseEntity<Map<String, String>> {
        val pessoa = pessoaRepository.findByEmail(email)

        return if (pessoa != null) {
            val linkRedefinicao = "http://localhost:3000/forgetpassword?email=$email"

            val mensagem = """
                <p>Olá, ${pessoa.nome}!</p>
                <p>Clique no link abaixo para redefinir sua senha:</p>
                <a href="$linkRedefinicao">Redefinir Senha</a>
            """.trimIndent()

            emailService.enviarEmail(
                destinatario = email,
                assunto = "Redefinição de Senha - Projeto Solidário",
                corpo = mensagem
            )

            ResponseEntity.ok(mapOf("mensagem" to "E-mail de redefinição enviado com sucesso."))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("erro" to "E-mail não encontrado."))
        }
    }

}

data class LoginRequest(
    val email: String,
    val senha: String
)
