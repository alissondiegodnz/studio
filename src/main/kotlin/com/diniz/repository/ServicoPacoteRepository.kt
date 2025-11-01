package com.diniz.repository

import com.diniz.domain.ServicoPacote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServicoPacoteRepository : JpaRepository<ServicoPacote, Long> {
}