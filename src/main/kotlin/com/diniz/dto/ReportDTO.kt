package com.diniz.dto

import java.math.BigDecimal

data class ReportDTO (
    val todayRevenue: BigDecimal?,
    val totalRevenue: BigDecimal,
    val averagePayment: BigDecimal,
    val totalServices: Long,
    val dailyRevenue: List<ReportDailyRevenueDTO>?,
    val revenueByCategory: List<ReportRevenueByCategoryDTO>?,
    val revenueByProfessional: List<ReportRevenueByProfessionalDTO>?,
    val paymentMethods: List<ReportPaymentMethodsDTO>?
) {
    constructor(totalRevenue: BigDecimal, averagePayment: BigDecimal, totalServices: Long) : this(
        null, totalRevenue, averagePayment, totalServices, null, null, null, null
    )
}