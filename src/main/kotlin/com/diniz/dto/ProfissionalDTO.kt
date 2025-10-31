package com.diniz.dto

import com.diniz.domain.Profissional

data class ProfissionalDTO(
    val id: String?,
    val name: String,
    val phone: String,
    val status: String,
    val address: String
) {
    companion object {
        fun from(profissional: Profissional): ProfissionalDTO {
            return ProfissionalDTO(
                id = profissional.id.toString(),
                name = profissional.name,
                phone = profissional.phone,
                status = profissional.status,
                address = profissional.address
            )
        }
    }
}