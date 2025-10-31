package com.diniz.controller

import com.diniz.domain.Pagamento
import com.diniz.dto.PagamentoDTO
import com.diniz.helper.DateHelper
import com.diniz.repository.ClienteRepository
import com.diniz.repository.PagamentoRepository
import com.diniz.repository.ProfissionalRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/services")
class PagamentoController(
    private val repository: PagamentoRepository,
    private val clienteRepository: ClienteRepository,
    private val profissionalRepository: ProfissionalRepository,
    private val dateHelper: DateHelper
) {
    
    @GetMapping
    fun findAll(startDate: String?, endDate: String?, category: String?, professionalId: String?): List<PagamentoDTO> {
        val paramStartDate = dateHelper.obtenhaDataHoraInicioDoDia(startDate)
        val paramEndDate = dateHelper.obtenhaDataHoraFimDoDia(endDate)
        val pagamentos = repository.findAllByFilters(paramStartDate, paramEndDate, category, if (professionalId.isNullOrBlank()) null else professionalId.toLong())

        return pagamentos.map {
            PagamentoDTO.from(it)
        }
    }
    
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<PagamentoDTO> {
        return repository.findById(id)
            .map { pagamento -> ResponseEntity.ok(PagamentoDTO.from(pagamento)) }
            .orElse(ResponseEntity.notFound().build())
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: PagamentoDTO): PagamentoDTO {
        dateHelper.valideDataHora(dto)
        val novoPagamento = Pagamento(
            cliente = clienteRepository.getReferenceById(dto.clientId.toLong()),
            profissional = profissionalRepository.getReferenceById(dto.professionalId.toLong()),
            categoria = dto.category,
            valor = dto.value,
            metodoPagamento = dto.paymentMethod,
            data = dateHelper.obtenhaDataHora(dto.date, dto.time),
            descricao = dto.description
        )
        val created = repository.save(novoPagamento)
        return PagamentoDTO.from(created)
    }
    
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: PagamentoDTO): ResponseEntity<PagamentoDTO> {
        dateHelper.valideDataHora(dto)
        return repository.findById(id).map { existingPagamento ->
            existingPagamento.cliente = clienteRepository.getReferenceById(dto.clientId.toLong())
            existingPagamento.profissional = profissionalRepository.getReferenceById(dto.professionalId.toLong())
            existingPagamento.categoria = dto.category
            existingPagamento.valor = dto.value
            existingPagamento.metodoPagamento = dto.paymentMethod
            existingPagamento.data = dateHelper.obtenhaDataHora(dto.date, dto.time)
            existingPagamento.descricao = dto.description
            val savedPagamento = repository.save(existingPagamento)
            ResponseEntity.ok(PagamentoDTO.from(savedPagamento))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

}