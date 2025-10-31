package com.diniz.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "cliente")
class Cliente (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,
    var phone: String,
    var email: String,
    var birthDate: LocalDate?,
    var observations: String
) {
    constructor(): this(null, "", "", "", null, "")
}