package com.diniz.repository

import com.diniz.domain.Pacote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PacoteRepository : JpaRepository<Pacote, Long> {
}