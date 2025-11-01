package com.diniz.dto

import com.diniz.domain.MetodoPagamento
import java.math.BigDecimal

data class MetodoPagamentoDTO(
    val id: String?,
    val paymentMethod: String,
    val value: BigDecimal
) {
    companion object {
        fun from(metodoPagamento: MetodoPagamento): MetodoPagamentoDTO {
            return MetodoPagamentoDTO(
                id = metodoPagamento.id.toString(),
                paymentMethod = metodoPagamento.metodoPagamento,
                value = metodoPagamento.valor
            )
        }
    }
}