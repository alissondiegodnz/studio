package com.diniz.dto

import java.math.BigDecimal

data class ReportDTO (
    val todayRevenue: BigDecimal?,
    val totalRevenue: BigDecimal,
    val totalPayments: Long,
    val totalServices: Long,
    val dailyRevenue: List<ReportDailyRevenueDTO>?,
    val revenueByCategory: List<ReportRevenueByCategoryDTO>?,
    val revenueByProfessional: List<ReportRevenueByProfessionalDTO>?,
    val paymentMethods: List<ReportPaymentMethodsDTO>?
) {
    constructor(totalRevenue: BigDecimal, totalPayments: Long, totalServices: Long) : this(
        null, totalRevenue, totalPayments, totalServices, null, null, null, null
    )
}