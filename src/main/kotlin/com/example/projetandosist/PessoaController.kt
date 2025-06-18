package com.example.projetandosist

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
@RequestMapping("/pessoas")
class PessoaController(
    private val pessoaRepository: PessoaRepository,
    private val emailService: EmailService
) {


    @GetMapping
    fun listar(): ResponseEntity<List<Pessoa>> {
        val pessoas = pessoaRepository.findAll()
        return ResponseEntity.ok(pessoas)
    }

    @GetMapping("/{email}/imagem")
    fun getImagemPerfil(@PathVariable email: String): ResponseEntity<ByteArray> {
        val pessoa = pessoaRepository.findByEmail(email)
            ?: return ResponseEntity.notFound().build()

        return pessoa.imagemPerfil?.let {
            ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(it)
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun criar(
        @RequestParam nome: String,
        @RequestParam email: String,
        @RequestParam senha: String,
        @RequestParam confirmarsenha: String,
        @RequestPart(value = "imagemPerfil", required = false) imagemPerfil: MultipartFile?
    ): ResponseEntity<Any> {
        if (senha != confirmarsenha) {
            return ResponseEntity
                .badRequest()
                .body(mapOf("erro" to "Senha e confirmação não conferem."))
        }

        if (pessoaRepository.findByEmail(email) != null) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(mapOf("erro" to "E-mail já cadastrado."))
        }

        val pessoa = Pessoa(
            nome = nome,
            email = email,
            senha = senha,
            confirmarSenha = confirmarsenha,
            tipoDeUsuario = TipoDeUsuario.USUARIO_COMUM,
            imagemPerfil = imagemPerfil?.bytes
        )

        val pessoaSalva = pessoaRepository.save(pessoa)

        return ResponseEntity
            .created(URI.create("/pessoas/${pessoaSalva.id}"))
            .body(pessoaSalva)
    }

    // ✅ Redefinir senha (envia e-mail com link)
    @PostMapping("/redefinir-senha")
    fun redefinirSenha(@RequestParam email: String): ResponseEntity<Any> {
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

    // ✅ Buscar pessoa por ID (boas práticas)
    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<Pessoa> {
        val pessoa = pessoaRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(pessoa)
    }

    // ✅ Deletar pessoa por ID (boas práticas)
    @DeleteMapping("/{id}")
    fun deletar(@PathVariable id: Long): ResponseEntity<Void> {
        val pessoa = pessoaRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        pessoaRepository.delete(pessoa)
        return ResponseEntity.noContent().build()
    }

    data class LoginDTO(
        val email: String,
        val senha: String
    )
}
