package com.diniz.domain

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "metodo_pagamento")
data class MetodoPagamento (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "pagamento_id")
    var pagamento: Pagamento,

    var metodoPagamento: String,
    var valor: BigDecimal

) {
    constructor(): this(null, Pagamento(), "", BigDecimal.ZERO)

    constructor(metodoPagamento: String, valor: BigDecimal):
            this(null, Pagamento(), metodoPagamento, valor)
}