package com.example.projetandosist

import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
@RequestMapping("/doacoes")
@CrossOrigin(origins = "https://front-end25v8.onrender.com")
class DoacaoController(
    private val doacoesRepository: DoacoesRepository,
    private val pessoaRepository: PessoaRepository
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun realizarDoacao(
        @RequestParam descricao: String,
        @RequestParam quantidade: Int,
        @RequestParam email: String,
        @RequestParam(value = "imagem_url", required = false) imagemUrl: String?,
        session: HttpSession
    ): ResponseEntity<Any> {
        val username = session.getAttribute("username") as? String ?: "Desconhecido"

        val doacao = Doacao(
            descricao = descricao,
            quantidade = quantidade,
            username = username,
            email = email,
            imagemUrl = imagemUrl
        )

        val doacaoSalva = doacoesRepository.save(doacao)

        return ResponseEntity
            .created(URI.create("/doacoes/${doacaoSalva.id}"))
            .body(doacaoSalva)
    }

    // ✅ Visualizar doações por e-mail (usuário específico)
    @GetMapping("/usuario")
    fun visualizarMinhasDoacoes(@RequestParam email: String): ResponseEntity<List<Doacao>> {
        val doacoesUsuario = doacoesRepository.findByEmail(email)
        return ResponseEntity.ok(doacoesUsuario)
    }

    @GetMapping
    fun visualizarTodasDoacoes(): ResponseEntity<List<Doacao>> {
        val todasAsDoacoes = doacoesRepository.findAll()
        return ResponseEntity.ok(todasAsDoacoes)
    }

    @GetMapping("/doadores")
    fun visualizarDoadores(): ResponseEntity<List<Pessoa>> {
        val doadores = pessoaRepository.findAll()
        return ResponseEntity.ok(doadores)
    }
}
