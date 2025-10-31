package com.diniz.dto

import com.diniz.domain.Agendamento
import com.diniz.helper.DateHelper
import java.time.format.DateTimeFormatter

data class AgendamentoDTO(
    val id: String?,
    val clientId: String,
    val clientName: String,
    val professionalId: String?,
    val professionalName: String?,
    val category: String,
    val status: String,
    override val date: String,
    override val time: String,
    val service: String?,
    val observations: String?
) : DataHoraInterface {
    companion object {
        fun from(agendamento: Agendamento): AgendamentoDTO {
            return AgendamentoDTO(
                id = agendamento.id.toString(),
                clientId = agendamento.cliente.id.toString(),
                clientName = agendamento.cliente.name,
                professionalId = agendamento.profissional?.id?.toString(),
                professionalName = agendamento.profissional?.name,
                category = agendamento.categoria,
                status = agendamento.status,
                date = agendamento.data.format(DateTimeFormatter.ofPattern(DateHelper.FORMATO_YYYY_MM_DD)),
                time = agendamento.data.format(DateTimeFormatter.ofPattern(DateHelper.FORMATO_HH_MM)),
                service = agendamento.servico,
                observations = agendamento.observacoes
            )
        }
    }
}