package com.diniz.repository

import com.diniz.domain.ServicoPagamento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServicoPagamentoRepository : JpaRepository<ServicoPagamento, Long> {
}