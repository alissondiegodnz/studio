package com.diniz.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "pagamento")
data class Pagamento (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    var cliente: Cliente,

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    var profissional: Profissional,

    var categoria: String,
    var valor: BigDecimal,
    var metodoPagamento: String,
    var data: LocalDateTime,
    var descricao: String

) {
    constructor(): this(null, Cliente(), Profissional(), "", BigDecimal.ZERO, "", LocalDateTime.now(), "")
}