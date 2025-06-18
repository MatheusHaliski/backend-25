package com.example.projetandosist

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/relatorio")
@CrossOrigin(origins = ["https://front-end25v8.onrender.com"])
class RelatorioController(
    private val doacoesRepository: DoacoesRepository
) {
    @GetMapping("/itens-por-categoria")
    fun itensPorCategoria(): List<RelatorioItensPorCategoriaDTO> {
        return doacoesRepository.totalItensPorCategoria()
    }
    @GetMapping("/doacoes-por-usuario")
    fun doacoesPorUsuario(): List<RelatorioDoacoesPorUsuarioDTO> {
        return doacoesRepository.totalDoacoesPorUsuario()
    }
    @GetMapping("/itens-por-descricao")
    fun itensPorDescricao(): List<RelatorioItensPorDescricaoDTO> {
        return doacoesRepository.totalItensPorDescricao()
    }
}
