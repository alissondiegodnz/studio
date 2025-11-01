package com.diniz.domain

import jakarta.persistence.*
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
    @JoinColumn(name = "pacote_id")
    var pacote: Pacote?,

    @OneToMany(
        mappedBy = "pagamento",
        cascade = [CascadeType.ALL],
        fetch = FetchType.EAGER,
        orphanRemoval = true
    )
    var servicosPagamento: MutableList<ServicoPagamento> = mutableListOf(),

    var metodoPagamento: String,
    var data: LocalDateTime,
    var descricao: String,
    var tipoDeServico: String

) {
    constructor(): this(null, Cliente(), Pacote(), mutableListOf(), "", LocalDateTime.now(), "", "")
}