package com.diniz.domain

import jakarta.persistence.*

@Entity
@Table(name = "pacote")
class Pacote (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToMany(mappedBy = "pacote", cascade = [CascadeType.ALL], orphanRemoval = true)
    var itens: MutableSet<ServicoPacote> = mutableSetOf(),

    var nome: String,
    var status: String,
    var descricao: String?,

) {
    constructor(): this(null, mutableSetOf(), "", "", "")
}