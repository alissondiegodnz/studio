package com.diniz.domain

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "servico_pagamento")
class ServicoPagamento (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "servico_id")
    var servico: Servico,

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    var profissional: Profissional,

    @ManyToOne
    @JoinColumn(name = "pagamento_id")
    var pagamento: Pagamento,

    var valor: BigDecimal,
    var ehPagamentoPacote: Boolean
) {
    constructor(): this(null, Servico(), Profissional(), Pagamento(), BigDecimal.ZERO, false)

    constructor(servico: Servico, profissional: Profissional, valor: BigDecimal, ehPagamentoPacote: Boolean):
            this(null, servico, profissional, Pagamento(), valor, ehPagamentoPacote)
}