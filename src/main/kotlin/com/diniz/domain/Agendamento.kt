package com.diniz.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "agendamento")
data class Agendamento (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    var cliente: Cliente,

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    var profissional: Profissional?,

    var categoria: String,
    var status: String,
    var data: LocalDateTime,
    var servico: String?,
    var observacoes: String?

) {
    constructor(): this(null, Cliente(), Profissional(), "", "", LocalDateTime.now(), "", "")
}