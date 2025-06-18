package com.example.projetandosist

import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/perfil")
@CrossOrigin(origins = ["http://localhost:3000"])
class PerfilController(
    private val pessoaRepository: PessoaRepository
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun salvarDadosPerfil(
        @RequestPart dados: DadosPerfilDTO,
        @RequestPart(value = "imagemPerfil", required = false) imagemPerfil: MultipartFile?,
        session: HttpSession
    ): ResponseEntity<Any> {
        val pessoa = pessoaRepository.findByEmail(dados.emailUsuario)

        return if (pessoa != null) {
            pessoa.nome = dados.nome
            pessoa.email = dados.email

            dados.tipoDeUsuario?.let { pessoa.tipoDeUsuario = it }

            if (!dados.senha.isNullOrBlank()) {
                pessoa.senha = dados.senha
                pessoa.confirmarSenha = dados.senha
            }

            imagemPerfil?.takeIf { !it.isEmpty }?.let {
                pessoa.imagemPerfil = it.bytes
            }

            pessoaRepository.save(pessoa)
            session.setAttribute("username", pessoa.nome)

            ResponseEntity.ok(mapOf("mensagem" to "Perfil atualizado com sucesso"))
        } else {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("erro" to "Usuário não encontrado"))
        }
    }

    @GetMapping("/logout")
    fun logout(session: HttpSession): ResponseEntity<Map<String, String>> {
        session.invalidate()
        return ResponseEntity.ok(mapOf("mensagem" to "Logout realizado com sucesso"))
    }
}
