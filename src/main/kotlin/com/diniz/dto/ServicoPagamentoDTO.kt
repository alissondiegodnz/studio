package com.diniz.dto

import com.diniz.domain.ServicoPagamento
import java.math.BigDecimal

data class ServicoPagamentoDTO(
    val id: String?,
    val serviceId: String,
    val serviceName: String,
    val value: BigDecimal,
    val professionalId: String,
    val professionalName: String,
    val isPackageService: Boolean
) {
    companion object {
        fun from(servicoPagamento: ServicoPagamento): ServicoPagamentoDTO {
            return ServicoPagamentoDTO(
                id = servicoPagamento.id.toString(),
                serviceId = servicoPagamento.servico.id.toString(),
                serviceName = servicoPagamento.servico.nome,
                value = servicoPagamento.valor,
                professionalId = servicoPagamento.profissional.id.toString(),
                professionalName = servicoPagamento.profissional.name,
                isPackageService = servicoPagamento.ehPagamentoPacote
            )
        }
    }
}