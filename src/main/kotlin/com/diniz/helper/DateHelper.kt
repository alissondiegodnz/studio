package com.diniz.helper

import com.diniz.dto.DataHoraInterface
import com.diniz.dto.PagamentoDTO
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class DateHelper {

    companion object {
        val FORMATO_YYYY_MM_DD = "yyyy-MM-dd"
        val FORMATO_HH_MM = "HH:mm"
        val FORMATO_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
        val FORMATO_DD_MM_YYYY = "dd/MM/yyyy"
        val DATE_TIME_FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern(FORMATO_YYYY_MM_DD)
        val DATE_TIME_FORMATTER_DD_MM_YYYY = DateTimeFormatter.ofPattern(FORMATO_DD_MM_YYYY)
    }

    fun obtenhaObjetoData_DD_MM_YYYY(data: String): LocalDate? {
        return if (data != "") LocalDate.parse(data, DateTimeFormatter.ofPattern(FORMATO_YYYY_MM_DD)) else null
    }

    fun obtenhaDataString_YYYY_MM_DD(data: LocalDate?): String {
        return if (data == null) "" else data.format(DateTimeFormatter.ofPattern(FORMATO_YYYY_MM_DD))
    }

    fun obtenhaDataString_DD_MM_YYYY(data: LocalDate?): String {
        return if (data == null) "" else data.format(DateTimeFormatter.ofPattern(FORMATO_DD_MM_YYYY))
    }

    fun obtenhaDataHora(data: String, hora: String): LocalDateTime {
        return LocalDateTime.parse("$data $hora", DateTimeFormatter.ofPattern(FORMATO_YYYY_MM_DD_HH_MM))
    }

    fun valideDataHora(dto: DataHoraInterface) {
        if (dto.date.isBlank() || dto.time.isBlank()) {
            throw Exception("Data e hora são obrigatórios para cadastro")
        }
    }

    fun obtenhaDataHoraInicioDoDia(data: String?): LocalDateTime? {
        return if (data.isNullOrBlank()) null else LocalDate.parse(data, DateTimeFormatter.ofPattern(DateHelper.FORMATO_YYYY_MM_DD)).atStartOfDay()
    }

    fun obtenhaDataHoraFimDoDia(data: String?): LocalDateTime? {
        return if (data.isNullOrBlank()) null else LocalDate.parse(data, DateTimeFormatter.ofPattern(DateHelper.FORMATO_YYYY_MM_DD)).atTime(23, 59, 59)
    }

    fun formatePara_DD_MM_YYYY(data: String, formato: DateTimeFormatter): String {
        return LocalDate.parse(data, formato).format(DATE_TIME_FORMATTER_DD_MM_YYYY)
    }

}