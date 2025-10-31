package com.diniz.dto

import java.math.BigDecimal

data class ReportPaymentMethodsDTO (
    val method: String,
    val value: BigDecimal
)