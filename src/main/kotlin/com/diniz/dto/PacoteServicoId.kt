package com.diniz.dto

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class PacoteServicoId(
    @Column(name = "pacote_id")
    var pacoteId: Long?,

    @Column(name = "servico_id")
    var servicoId: Long?

) : Serializable {
    constructor() : this(null, null)
}