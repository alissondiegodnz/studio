package com.diniz.repository

import com.diniz.domain.Servico
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServicoRepository : JpaRepository<Servico, Long> {
}