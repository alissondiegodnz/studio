package com.diniz.repository

import com.diniz.domain.Pagamento
import com.diniz.dto.ReportDTO
import com.diniz.dto.ReportDailyRevenueDTO
import com.diniz.dto.ReportDailyRevenueProjection
import com.diniz.dto.ReportPaymentMethodsDTO
import com.diniz.dto.ReportRevenueByCategoryDTO
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

//    @Query("""
//    SELECT COALESCE(SUM(p.valor), 0)                  AS totalRevenue,
//        CAST(COALESCE(AVG(p.valor), 0) AS BigDecimal) AS averagePayment,
//        CAST(COALESCE(COUNT(p.id), 0) AS Long)        AS totalServices
//    FROM Pagamento p
//    WHERE p.data >= :startDate
//      AND p.data <= :endDate
//      AND (:category IS NULL OR p.categoria = :category)
//      AND (:profissional IS NULL OR p.profissional.id = :profissional)
//    """)
//    fun getReportInfo(
//        startDate: LocalDateTime,
//        endDate: LocalDateTime,
//        category: String?,
//        profissional: Long?
//    ): ReportDTO
//
//    @Query("""
//        SELECT COALESCE(SUM(p.valor), 0) AS todayRevenue
//        FROM Pagamento p
//        WHERE p.data BETWEEN :startDate AND :endDate
//            AND (:category IS NULL OR p.categoria = :category)
//            AND (:profissional IS NULL OR p.profissional.id = :profissional)
//    """)
//    fun getTodayRevenue(
//        category: String?,
//        profissional: Long?,
//        startDate: LocalDateTime = LocalDate.now().atStartOfDay(),
//        endDate: LocalDateTime = LocalDate.now().atStartOfDay().plusDays(1)
//    ): BigDecimal
//
//    @Query(value = """
//        SELECT STRFTIME('%Y-%m-%d', data / 1000, 'unixepoch', '-3 hours') AS date,
//               CAST(COALESCE(SUM(valor), 0) AS REAL)          AS value
//        FROM pagamento
//        WHERE data >= :startDate
//          AND data <= :endDate
//          AND (:category IS NULL OR categoria = :category)
//          AND (:profissional IS NULL OR profissional_id = :profissional)
//        GROUP BY STRFTIME('%Y-%m-%d', data / 1000, 'unixepoch', '-3 hours')
//        ORDER BY STRFTIME('%Y-%m-%d', data / 1000, 'unixepoch', '-3 hours')
//    """, nativeQuery = true)
//    fun getDailyRevenue(
//        startDate: LocalDateTime,
//        endDate: LocalDateTime,
//        category: String?,
//        profissional: Long?
//    ): List<ReportDailyRevenueProjection>
//
//    @Query(value = """
//        SELECT
//            p.categoria                           AS category,
//            CAST(COALESCE(SUM(valor), 0) AS REAL) AS value,
//            CAST(SUM(p.valor) AS REAL) * 100.0 /
//                SUM(SUM(p.valor)) OVER ()         AS percentage
//        FROM pagamento p
//        WHERE data >= :startDate
//          AND data <= :endDate
//          AND (:category IS NULL OR categoria = :category)
//          AND (:profissional IS NULL OR profissional_id = :profissional)
//        GROUP BY p.categoria
//    """, nativeQuery = true)
//    fun getRevenueByCategory(
//        startDate: LocalDateTime,
//        endDate: LocalDateTime,
//        category: String?,
//        profissional: Long?
//    ): List<ReportRevenueByCategoryProjection>
//
//    @Query("""
//        SELECT p.profissional.name                AS name,
//           CAST(COALESCE(COUNT(p.id), 0) AS Long) AS services,
//            COALESCE(SUM(p.valor), 0)             AS total
//        FROM Pagamento p
//        WHERE p.data >= :startDate
//          AND p.data <= :endDate
//          AND (:category IS NULL OR p.categoria = :category)
//          AND (:profissional IS NULL OR p.profissional.id = :profissional)
//        GROUP BY p.profissional.name
//        ORDER BY p.profissional.name
//    """)
//    fun getRevenueByProfessional(
//        startDate: LocalDateTime,
//        endDate: LocalDateTime,
//        category: String?,
//        profissional: Long?
//    ): List<ReportRevenueByProfessionalDTO>
//
//    @Query("""
//        SELECT p.metodoPagamento      AS method,
//	        COALESCE(SUM(p.valor), 0) AS value
//        FROM Pagamento p
//        WHERE p.data >= :startDate
//          AND p.data <= :endDate
//          AND (:category IS NULL OR p.categoria = :category)
//          AND (:profissional IS NULL OR p.profissional.id = :profissional)
//        GROUP BY p.metodoPagamento
//    """)
//    fun getRevenueByPaymentMethod(
//        startDate: LocalDateTime,
//        endDate: LocalDateTime,
//        category: String?,
//        profissional: Long?
//    ): List<ReportPaymentMethodsDTO>

}