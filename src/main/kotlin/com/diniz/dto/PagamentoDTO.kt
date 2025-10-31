package com.diniz.dto

import com.diniz.domain.Pagamento
import com.diniz.helper.DateHelper
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import kotlin.String

data class PagamentoDTO(
    val id: String?,
    val clientId: String,
    val clientName: String,
    val professionalId: String,
    val professionalName: String,
    val category: String,
    val value: BigDecimal,
    val paymentMethod: String,
    override val date: String,
    override val time: String,
    val description: String
) : DataHoraInterface {
    companion object {
        fun from(pagamento: Pagamento): PagamentoDTO {
            return PagamentoDTO(
                id = pagamento.id.toString(),
                clientId = pagamento.cliente.id.toString(),
                clientName = pagamento.cliente.name,
                professionalId = pagamento.profissional.id.toString(),
                professionalName = pagamento.profissional.name,
                category = pagamento.categoria,
                value = pagamento.valor,
                paymentMethod = pagamento.metodoPagamento,
                date = pagamento.data.format(DateTimeFormatter.ofPattern(DateHelper.FORMATO_YYYY_MM_DD)),
                time = pagamento.data.format(DateTimeFormatter.ofPattern(DateHelper.FORMATO_HH_MM)),
                description = pagamento.descricao
            )
        }
    }
}