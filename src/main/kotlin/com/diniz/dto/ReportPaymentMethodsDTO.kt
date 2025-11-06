package com.diniz.dto

import java.math.BigDecimal

data class ReportPaymentMethodsDTO (
    var method: String,
    val value: BigDecimal
)