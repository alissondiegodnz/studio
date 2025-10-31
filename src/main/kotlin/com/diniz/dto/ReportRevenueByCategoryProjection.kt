package com.diniz.dto

data class ReportRevenueByCategoryProjection (
    val category: String,
    val value: Double,
    val percentage: Double
)