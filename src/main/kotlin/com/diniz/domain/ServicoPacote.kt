package com.diniz.domain

import com.diniz.dto.PacoteServicoId
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "servico_pacote")
class ServicoPacote(

    @EmbeddedId
    var id: PacoteServicoId? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("servicoId")
    @JoinColumn(name = "servico_id")
    var servico: Servico,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pacoteId")
    @JoinColumn(name = "pacote_id")
    var pacote: Pacote,

    var valor: BigDecimal

) {
    constructor(): this(PacoteServicoId(), Servico(), Pacote(), BigDecimal.ZERO)

    constructor(servico: Servico, valor: BigDecimal):
            this(PacoteServicoId(), servico, Pacote(), valor)
}