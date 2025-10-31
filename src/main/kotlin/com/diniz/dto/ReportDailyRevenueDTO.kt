package com.diniz.dto

import java.math.BigDecimal

data class ReportDailyRevenueDTO (
    val date: String,
    val value: BigDecimal,
)