package com.diniz.domain

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "servico")
class Servico (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var nome: String,
    var categoria: String,
    var status: String,
    var valor: BigDecimal?,
    var descricao: String?

) {
    constructor(): this(null, "", "", "", null, null)
}