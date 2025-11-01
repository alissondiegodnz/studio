package com.diniz.controller

import com.diniz.domain.Pagamento
import com.diniz.dto.PagamentoDTO
import com.diniz.helper.DateHelper
import com.diniz.repository.ClienteRepository
import com.diniz.repository.PacoteRepository
import com.diniz.repository.PagamentoRepository
import com.diniz.service.PagamentoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/payments")
class PagamentoController(
    private val pagamentoService: PagamentoService,
) {
    
    @GetMapping
    fun getAll(startDate: String?, endDate: String?, category: String?, professionalId: String?): List<PagamentoDTO> {
        return pagamentoService.obtenhaPagamentos(startDate, endDate, category, professionalId)
    }

        @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PagamentoDTO> {
        val pagamento = pagamentoService.obtenhaPagamento(id)
        return if (pagamento != null) ResponseEntity.ok(pagamento) else ResponseEntity.notFound().build()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: PagamentoDTO): ResponseEntity<Any> {
        try {
            pagamentoService.crieNovoPagamento(dto)
            return ResponseEntity.noContent().build()
        } catch (ex: Exception) {
            throw Exception("Erro ao criar o pagamento: ${ex.message}")
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: PagamentoDTO): ResponseEntity<Any> {
        val savedPagamento = pagamentoService.atualizePagamento(id, dto)
        if (savedPagamento != null) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        return if (pagamentoService.removaPagamento(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

}