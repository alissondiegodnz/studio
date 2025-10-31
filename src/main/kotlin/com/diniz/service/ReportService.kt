package com.diniz.service

import com.diniz.dto.ReportDTO
import com.diniz.dto.ReportDailyRevenueDTO
import com.diniz.dto.ReportRevenueByCategoryDTO
import com.diniz.helper.DateHelper
import com.diniz.repository.PagamentoRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class ReportService(
    private val pagamentoRepository: PagamentoRepository,
    private val dateHelper: DateHelper
) {

    @Cacheable(value = ["buildReports"])
    fun buildReportDTO(params: Map<String, String?>): ReportDTO {
        val paramStartDate = dateHelper.obtenhaDataHoraInicioDoDia(params["startDate"])!!
        val paramEndDate = dateHelper.obtenhaDataHoraFimDoDia(params["endDate"])!!
        val paramCategoria = params["category"]
        val paramIdProfissional = params["professionalId"].run { if (isNullOrBlank()) null else toLong() }

        val reportInfo = pagamentoRepository.getReportInfo(paramStartDate, paramEndDate, paramCategoria, paramIdProfissional)
        val todayRevenue = pagamentoRepository.getTodayRevenue(paramCategoria, paramIdProfissional)

        val dailyRevenueProjectionList = pagamentoRepository.getDailyRevenue(paramStartDate, paramEndDate, paramCategoria, paramIdProfissional)
        val dailyRevenueDTOList = dailyRevenueProjectionList.map {
            ReportDailyRevenueDTO(
                dateHelper.formatePara_DD_MM_YYYY(it.date, DateHelper.DATE_TIME_FORMATTER_YYYY_MM_DD),
                BigDecimal.valueOf(it.value).setScale(2, RoundingMode.HALF_UP)
            )
        }

        val revenueByCategoryProjectionList = pagamentoRepository.getRevenueByCategory(paramStartDate, paramEndDate, paramCategoria, paramIdProfissional)
        val revenueByCategoryDTOList = revenueByCategoryProjectionList.map {
            ReportRevenueByCategoryDTO(
                it.category,
                BigDecimal.valueOf(it.value).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(it.percentage).setScale(2, RoundingMode.HALF_UP)
            )
        }

        val revenueByProfessionalDTOList = pagamentoRepository.getRevenueByProfessional(paramStartDate, paramEndDate, paramCategoria, paramIdProfissional)
        val revenueByPaymentMethodsDTOList = pagamentoRepository.getRevenueByPaymentMethod(paramStartDate, paramEndDate, paramCategoria, paramIdProfissional)

        return ReportDTO(
            todayRevenue, reportInfo.totalRevenue, reportInfo.averagePayment, reportInfo.totalServices,
            dailyRevenueDTOList, revenueByCategoryDTOList, revenueByProfessionalDTOList, revenueByPaymentMethodsDTOList
        )
//        return ReportDTO(
//            todayRevenue, reportInfo.totalRevenue, reportInfo.averagePayment, reportInfo.totalServices,
//            listOf(
//                ReportDailyRevenueDTO("01/10/2025", BigDecimal("200")),
//                ReportDailyRevenueDTO("02/10/2025", BigDecimal("150")),
//                ReportDailyRevenueDTO("03/10/2025", BigDecimal("500")),
//                ReportDailyRevenueDTO("04/10/2025", BigDecimal("50")),
//                ReportDailyRevenueDTO("05/10/2025", BigDecimal("250")),
//                ReportDailyRevenueDTO("06/10/2025", BigDecimal("700")),
//                ReportDailyRevenueDTO("07/10/2025", BigDecimal("300")),
//                ReportDailyRevenueDTO("08/10/2025", BigDecimal("350")),
//                ReportDailyRevenueDTO("09/10/2025", BigDecimal("200")),
//                ReportDailyRevenueDTO("10/10/2025", BigDecimal("150")),
//                ReportDailyRevenueDTO("11/10/2025", BigDecimal("500")),
//                ReportDailyRevenueDTO("12/10/2025", BigDecimal("50")),
//                ReportDailyRevenueDTO("13/10/2025", BigDecimal("250")),
//                ReportDailyRevenueDTO("14/10/2025", BigDecimal("700")),
//                ReportDailyRevenueDTO("15/10/2025", BigDecimal("300")),
//                ReportDailyRevenueDTO("16/10/2025", BigDecimal("350")),
//                ReportDailyRevenueDTO("17/10/2025", BigDecimal("200")),
//                ReportDailyRevenueDTO("18/10/2025", BigDecimal("150")),
//                ReportDailyRevenueDTO("19/10/2025", BigDecimal("500")),
//                ReportDailyRevenueDTO("20/10/2025", BigDecimal("50")),
//                ReportDailyRevenueDTO("21/10/2025", BigDecimal("150")),
//                ReportDailyRevenueDTO("22/10/2025", BigDecimal("500")),
//                ReportDailyRevenueDTO("23/10/2025", BigDecimal("50")),
//                ReportDailyRevenueDTO("24/10/2025", BigDecimal("250")),
//                ReportDailyRevenueDTO("25/10/2025", BigDecimal("700")),
//                ReportDailyRevenueDTO("26/10/2025", BigDecimal("300")),
//                ReportDailyRevenueDTO("27/10/2025", BigDecimal("110")),
//                ReportDailyRevenueDTO("28/10/2025", BigDecimal("200")),
//                ReportDailyRevenueDTO("29/10/2025", BigDecimal("900")),
//                ReportDailyRevenueDTO("30/10/2025", BigDecimal("1350"))
//            )
//            listOf(
//                ReportRevenueByCategoryDTO("Bronze", 400, 38),
//                ReportRevenueByCategoryDTO("Estética", 600, 60),
//            ),
//            listOf(
//                ReportRevenueByProfessionalDTO("Jessica", 1, 200, 400),
//                ReportRevenueByProfessionalDTO("DJennifer", 2, 300, 600),
//            ),
//            listOf(
//                ReportPaymentMethodsDTO("PIX", 800),
//                ReportPaymentMethodsDTO("Cartão de Crédito", 200),
//            )
//        )
    }
}