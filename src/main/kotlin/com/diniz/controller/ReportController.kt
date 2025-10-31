package com.diniz.controller

import com.diniz.dto.*
import com.diniz.service.ReportService
import org.springframework.data.repository.query.Param
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reports")
class ReportController(
    private val reportService: ReportService
) {

    @GetMapping
    fun gereReports(
        @Param("startDate") startDate: String,
        @Param("endDate") endDate: String,
        @Param("category") category: String?,
        @Param("professionalId") professionalId: String?,
        ): ReportDTO {
        val params = mapOf("startDate" to startDate, "endDate" to endDate, "category" to category, "professionalId" to professionalId)
        return reportService.buildReportDTO(params)
    }
}