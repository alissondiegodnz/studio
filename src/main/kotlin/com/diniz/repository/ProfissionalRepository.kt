package com.diniz.repository

import com.diniz.domain.Profissional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfissionalRepository : JpaRepository<Profissional, Long> {
}