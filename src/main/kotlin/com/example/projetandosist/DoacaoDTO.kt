package com.example.projetandosist

import java.util.*


class DoacaoDTO(doacao: Doacao) {
    private val id = doacao.id
    private val email = doacao.email
    private val descricao = doacao.descricao
    private val quantidade = doacao.quantidade
    private var imagemBase64: String? = null

    init {
        if (doacao.imagemObjeto != null) {
            this.imagemBase64 = Base64.getEncoder().encodeToString(doacao.imagemObjeto)
        }
    } // getters e setters
}
