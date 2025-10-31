package com.diniz.repository

import com.diniz.domain.Agendamento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AgendamentoRepository : JpaRepository<Agendamento, Long> {

    @Query("""
        SELECT a
        FROM Agendamento a
        WHERE (:categoria IS NULL OR a.categoria = :categoria)
          AND (:idProfissional IS NULL OR a.profissional.id = :idProfissional)
          AND (:startDate IS NULL OR a.data >= :startDate)
          AND (:endDate IS NULL OR a.data <= :endDate)
        ORDER BY a.data
    """)
    fun findAllByFilters(
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        categoria: String?,
        idProfissional: Long?
    ): List<Agendamento>
}