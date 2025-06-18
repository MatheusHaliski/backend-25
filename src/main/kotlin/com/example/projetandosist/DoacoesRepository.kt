package com.example.projetandosist

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal

interface DoacoesRepository : JpaRepository<Doacao, Long> {

    fun findByUsername(username: String): List<Doacao>
    fun findByEmail(email: String): List<Doacao>

    // 🔹 1. Relatório de doações por usuário
    @Query("""
        SELECT d.email AS username,
               COUNT(d) AS totalDoacoes,
               SUM(d.quantidade) AS totalItens,
               AVG(d.quantidade) AS mediaItensPorDoacao
        FROM Doacao d
        GROUP BY username
    """)
    fun totalDoacoesPorUsuario(): List<RelatorioDoacoesPorUsuarioDTO>

    // 🔹 2. Total de itens doados por descrição
    @Query("""
        SELECT d.descricao AS descricao, SUM(d.quantidade) AS totalItens
        FROM Doacao d
        GROUP BY d.descricao
    """)
    fun totalItensPorDescricao(): List<RelatorioItensPorDescricaoDTO>

    // 🔹 3. Total de itens doados por categoria
    @Query("""
        SELECT d.email AS categoria, SUM(d.quantidade) AS totalItens
        FROM Doacao d
        GROUP BY d.email
    """)
    fun totalItensPorCategoria(): List<RelatorioItensPorCategoriaDTO>


}


// 🔸 Interfaces DTOs (Projeções)
interface RelatorioDoacoesPorUsuarioDTO {
    val username: String
    val totalDoacoes: Long
    val totalItens: Long
    val mediaItensPorDoacao: Double
}

interface RelatorioItensPorDescricaoDTO {
    val descricao: String
    val totalItens: Long
}

interface RelatorioItensPorCategoriaDTO {
    val categoria: String
    val totalItens: Long
}
