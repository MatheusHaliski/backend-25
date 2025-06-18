package com.example.projetandosist

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class AtualizarImagensAoIniciar(
    private val produtoController: ProdutoController
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("🔄 Iniciando atualização das imagens dos produtos...")

        try {
            val resposta = produtoController.atualizarImagens()
            println("✅ ${resposta.body}")
        } catch (e: Exception) {
            println("❌ Erro ao atualizar imagens: ${e.message}")
        }
    }
}
