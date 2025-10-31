package com.diniz.dto

import java.math.BigDecimal

data class ReportRevenueByProfessionalDTO (
    val name: String,
    val services: Long,
    val total: BigDecimal
)