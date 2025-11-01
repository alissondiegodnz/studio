package com.diniz.repository

import com.diniz.domain.Pagamento
import com.diniz.dto.ReportDTO
import com.diniz.dto.ReportDailyRevenueProjection
import com.diniz.dto.ReportPaymentMethodsDTO
import com.diniz.dto.ReportRevenueByCategoryProjection
import com.diniz.dto.ReportRevenueByProfessionalDTO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface PagamentoRepository : JpaRepository<Pagamento, Long> {

    @Query("""
        SELECT DISTINCT p
        FROM Pagamento p
            JOIN ServicoPagamento sp ON sp.pagamento = p
            JOIN Servico s ON sp.servico = s
        WHERE (:startDate IS NULL OR p.data >= :startDate)
          AND (:endDate IS NULL OR p.data <= :endDate)
          AND (:categoria IS NULL OR s.categoria = :categoria)
          AND (:idProfissional IS NULL OR sp.profissional.id = :idProfissional)
        ORDER BY p.data DESC
    """)
    fun findAllByFilters(
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        categoria: String?,
        idProfissional: Long?
    ): List<Pagamento>

    @Query("""
    SELECT COALESCE(SUM(sp.valor), 0)                    AS totalRevenue,
        CAST(COALESCE(COUNT(DISTINCT p.id), 0) AS Long)  AS totalPayments,
        CAST(COALESCE(COUNT(DISTINCT sp.id), 0) AS Long) AS totalServices
    FROM Pagamento p
        JOIN p.servicosPagamento sp
    WHERE p.data >= :startDate
      AND p.data <= :endDate
      AND (:category IS NULL OR sp.servico.categoria = :category)
      AND (:profissional IS NULL OR sp.profissional.id = :profissional)
    """)
    fun getReportInfo(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        category: String?,
        profissional: Long?
    ): ReportDTO

    @Query("""
        SELECT COALESCE(SUM(sp.valor), 0) AS todayRevenue
        FROM Pagamento p
            JOIN p.servicosPagamento sp
        WHERE p.data BETWEEN :startDate AND :endDate
            AND (:category IS NULL OR sp.servico.categoria = :category)
            AND (:profissional IS NULL OR sp.profissional.id = :profissional)
    """)
    fun getTodayRevenue(
        category: String?,
        profissional: Long?,
        startDate: LocalDateTime = LocalDate.now().atStartOfDay(),
        endDate: LocalDateTime = LocalDate.now().atStartOfDay().plusDays(1)
    ): BigDecimal

    @Query(value = """
        SELECT STRFTIME('%Y-%m-%d', p.data / 1000, 'unixepoch', '-3 hours') AS date,
               CAST(COALESCE(SUM(sp.valor), 0) AS REAL)                     AS value
        FROM pagamento p
            JOIN servico_pagamento sp ON sp.pagamento_id = p.id
            JOIN servico s ON s.id = sp.servico_id
            JOIN profissional prof ON prof.id = sp.profissional_id
        WHERE data >= :startDate
          AND data <= :endDate
          AND (:category IS NULL OR s.categoria = :category)
          AND (:profissional IS NULL OR prof.id = :profissional)
        GROUP BY STRFTIME('%Y-%m-%d', p.data / 1000, 'unixepoch', '-3 hours')
        ORDER BY STRFTIME('%Y-%m-%d', p.data / 1000, 'unixepoch', '-3 hours')
    """, nativeQuery = true)
    fun getDailyRevenue(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        category: String?,
        profissional: Long?
    ): List<ReportDailyRevenueProjection>

    @Query(value = """
        SELECT
            s.categoria                              AS category,
            CAST(COALESCE(SUM(sp.valor), 0) AS REAL) AS value,
            CAST(SUM(sp.valor) AS REAL) * 100.0 /
                SUM(SUM(sp.valor)) OVER ()           AS percentage
        FROM pagamento p
            JOIN servico_pagamento sp ON sp.pagamento_id = p.id
            JOIN servico s ON s.id = sp.servico_id
            JOIN profissional prof ON prof.id = sp.profissional_id
        WHERE data >= :startDate
          AND data <= :endDate
          AND (:category IS NULL OR s.categoria = :category)
          AND (:profissional IS NULL OR prof.id = :profissional)
        GROUP BY s.categoria
    """, nativeQuery = true)
    fun getRevenueByCategory(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        category: String?,
        profissional: Long?
    ): List<ReportRevenueByCategoryProjection>

    @Query("""
        SELECT sp.profissional.name                         AS name,
           CAST(COALESCE(COUNT(DISTINCT sp.id), 0) AS Long) AS services,
           CAST(COALESCE(COUNT(DISTINCT p.id), 0) AS Long)  AS payments,
           COALESCE(SUM(sp.valor), 0)                       AS total
        FROM Pagamento p
            JOIN p.servicosPagamento sp
        WHERE p.data >= :startDate
          AND p.data <= :endDate
          AND (:category IS NULL OR sp.servico.categoria = :category)
          AND (:profissional IS NULL OR sp.profissional.id = :profissional)
        GROUP BY sp.profissional.name
        ORDER BY sp.profissional.name
    """)
    fun getRevenueByProfessional(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        category: String?,
        profissional: Long?
    ): List<ReportRevenueByProfessionalDTO>

    @Query("""
        SELECT mp.metodoPagamento      AS method,
	        COALESCE(SUM(mp.valor), 0) AS value
        FROM MetodoPagamento mp
        WHERE mp.pagamento.id IN (
            SELECT DISTINCT sp.pagamento.id
            FROM ServicoPagamento sp
            WHERE sp.pagamento.data >= :startDate
              AND sp.pagamento.data <= :endDate
              AND (:category IS NULL OR sp.servico.categoria = :category)
              AND (:profissional IS NULL OR sp.profissional.id = :profissional)
        )
        GROUP BY mp.metodoPagamento
    """)
    fun getRevenueByPaymentMethod(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        category: String?,
        profissional: Long?
    ): List<ReportPaymentMethodsDTO>

}