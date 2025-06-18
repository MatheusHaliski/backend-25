package com.example.projetandosist

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PessoaRepository : JpaRepository<Pessoa, Long> {
    fun findByEmailAndSenha(email: String, senha: String): Pessoa?
    fun findByEmail(email: String): Pessoa?
    fun findByNome(nome: String): Pessoa?
    }
