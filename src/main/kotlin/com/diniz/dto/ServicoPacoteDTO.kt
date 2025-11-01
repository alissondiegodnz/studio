package com.diniz.dto

import com.diniz.domain.Servico
import java.math.BigDecimal

data class ServicoPacoteDTO(
    val id: String?,
    val name: String,
    val category: String?,
    val status: String?,
    val price: BigDecimal?,
    val description: String?
) {
    companion object {
        fun from(servico: Servico): ServicoPacoteDTO {
            return ServicoPacoteDTO(
                id = servico.id.toString(),
                name = servico.nome,
                category = servico.categoria,
                status = servico.status,
                price = servico.valor,
                description = servico.descricao
            )
        }
    }
}