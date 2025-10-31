package com.diniz.domain

import jakarta.persistence.*

@Entity
@Table(name = "profissional")
class Profissional (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,
    var phone: String,
    var status: String,
    var address: String
) {
    constructor(): this(null, "", "", "", "")
}