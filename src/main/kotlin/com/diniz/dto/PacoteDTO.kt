package com.diniz.dto

import com.diniz.domain.Pacote
import java.math.BigDecimal

data class PacoteDTO(
    val id: String?,
    val name: String,
    val status: String,
    val description: String,
    val calculatedPrice: BigDecimal?,
    val services: List<ServicoDTO>,
) {
    companion object {
        fun from(pacote: Pacote): PacoteDTO {
            return PacoteDTO(
                id = pacote.id.toString(),
                name = pacote.nome,
                status = pacote.status,
                description = pacote.descricao ?: "",
                calculatedPrice = pacote.itens.sumOf { it.servico.valor ?: BigDecimal.ZERO },
                services = pacote.itens.map { ServicoDTO.from(it.servico) }
            )
        }
    }
}