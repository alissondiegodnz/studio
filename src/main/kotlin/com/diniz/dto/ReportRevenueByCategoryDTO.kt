package com.diniz.dto

import java.math.BigDecimal

data class ReportRevenueByCategoryDTO (
    val category: String,
    val value: BigDecimal,
    val percentage: BigDecimal
)