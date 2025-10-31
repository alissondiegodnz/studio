package com.diniz.controller

import com.diniz.domain.Agendamento
import com.diniz.dto.AgendamentoDTO
import com.diniz.helper.DateHelper
import com.diniz.repository.AgendamentoRepository
import com.diniz.repository.ClienteRepository
import com.diniz.repository.ProfissionalRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/appointments")
class AgendamentoController(
    private val repository: AgendamentoRepository,
    private val clienteRepository: ClienteRepository,
    private val profissionalRepository: ProfissionalRepository,
    private val dateHelper: DateHelper
) {
    
    @GetMapping
    fun findAll(startDate: String?, endDate: String?, category: String?, professionalId: String?): List<AgendamentoDTO> {
        val paramStartDate = dateHelper.obtenhaDataHoraInicioDoDia(startDate)
        val paramEndDate = dateHelper.obtenhaDataHoraFimDoDia(endDate)
        val agendamentos = repository.findAllByFilters(paramStartDate, paramEndDate, category, if (professionalId.isNullOrBlank()) null else professionalId.toLong())

        return agendamentos.map {
            AgendamentoDTO.from(it)
        }
    }
    
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<AgendamentoDTO> {
        return repository.findById(id)
            .map { agendamento -> ResponseEntity.ok(AgendamentoDTO.from(agendamento)) }
            .orElse(ResponseEntity.notFound().build())
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: AgendamentoDTO): AgendamentoDTO {
        dateHelper.valideDataHora(dto)
        val novoAgendamento = Agendamento(
            cliente = clienteRepository.getReferenceById(dto.clientId.toLong()),
            profissional = if (dto.professionalId?.toLongOrNull() != null) profissionalRepository.findById(dto.professionalId.toLong()).orElse(null) else null,
            categoria = dto.category,
            status = dto.status,
            data = dateHelper.obtenhaDataHora(dto.date, dto.time),
            servico = dto.service,
            observacoes = dto.observations
        )
        val created = repository.save(novoAgendamento)
        return AgendamentoDTO.from(created)
    }
    
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: AgendamentoDTO): ResponseEntity<AgendamentoDTO> {
        dateHelper.valideDataHora(dto)
        return repository.findById(id).map { existingAgendamento ->
            existingAgendamento.cliente = clienteRepository.getReferenceById(dto.clientId.toLong())
            existingAgendamento.profissional = if (dto.professionalId?.toLongOrNull() != null) profissionalRepository.findById(dto.professionalId.toLong()).orElse(null) else null
            existingAgendamento.categoria = dto.category
            existingAgendamento.status = dto.status
            existingAgendamento.data = dateHelper.obtenhaDataHora(dto.date, dto.time)
            existingAgendamento.servico = dto.service
            existingAgendamento.observacoes = dto.observations
            val savedAgendamento = repository.save(existingAgendamento)
            ResponseEntity.ok(AgendamentoDTO.from(savedAgendamento))
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