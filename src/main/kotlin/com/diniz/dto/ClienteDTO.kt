package com.diniz.dto

import com.diniz.domain.Cliente
import com.diniz.helper.DateHelper

data class ClienteDTO(
    val id: String?,
    val name: String,
    val phone: String,
    val email: String,
    val birthDate: String,
    val observations: String
) {
    companion object {
        fun from(cliente: Cliente, dateHelper: DateHelper): ClienteDTO {
            return ClienteDTO(
                id = cliente.id.toString(),
                name = cliente.name,
                phone = cliente.phone,
                email = cliente.email,
                birthDate = dateHelper.obtenhaDataString_YYYY_MM_DD(cliente.birthDate),
                observations = cliente.observations
            )
        }
    }
}