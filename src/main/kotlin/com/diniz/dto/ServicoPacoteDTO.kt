package com.diniz.dto

import com.diniz.domain.ServicoPacote
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
        fun from(servicoPacote: ServicoPacote): ServicoPacoteDTO {
            return ServicoPacoteDTO(
                id = servicoPacote.servico.id.toString(),
                name = servicoPacote.servico.nome,
                category = servicoPacote.servico.categoria,
                status = servicoPacote.servico.status,
                price = servicoPacote.valor,
                description = servicoPacote.servico.descricao
            )
        }
    }
}