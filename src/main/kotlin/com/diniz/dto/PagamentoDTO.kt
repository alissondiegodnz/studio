package com.diniz.dto

import com.diniz.domain.Pagamento
import com.diniz.helper.DateHelper
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

data class PagamentoDTO(
    val id: String?,
    val clientId: String,
    val clientName: String,
    val clientPhone: String,
    val value: BigDecimal,
    val isPartialValue: Boolean,
    val serviceType: String,
    val packageId: String,
    val packageName: String,
    val serviceLines: List<ServicoPagamentoDTO>,
    val paymentMethod: String,
    override val date: String,
    override val time: String,
    val description: String
) : DataHoraInterface {
    companion object {
        fun from(pagamento: Pagamento, valorFiltrado: BigDecimal, ehValorParcial: Boolean): PagamentoDTO {
            return PagamentoDTO(
                id = pagamento.id.toString(),
                clientId = pagamento.cliente.id.toString(),
                clientName = pagamento.cliente.name,
                clientPhone = pagamento.cliente.phone,
                value = valorFiltrado,
                isPartialValue = ehValorParcial,
                serviceType = pagamento.tipoDeServico,
                packageId = pagamento.pacote?.id?.toString() ?: "",
                packageName = pagamento.pacote?.nome ?: "",
                serviceLines = pagamento.servicosPagamento.map { ServicoPagamentoDTO.from(it)},
                paymentMethod = pagamento.metodoPagamento,
                date = pagamento.data.format(DateTimeFormatter.ofPattern(DateHelper.FORMATO_YYYY_MM_DD)),
                time = pagamento.data.format(DateTimeFormatter.ofPattern(DateHelper.FORMATO_HH_MM)),
                description = pagamento.descricao
            )
        }
    }
}