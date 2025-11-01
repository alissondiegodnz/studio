package com.diniz.dto

import java.math.BigDecimal

data class ReportRevenueByProfessionalDTO (
    val name: String,
    val services: Long,
    val payments: Long,
    val total: BigDecimal
)