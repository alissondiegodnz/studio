package com.diniz.controller

import com.diniz.domain.Cliente
import com.diniz.dto.ClienteDTO
import com.diniz.helper.DateHelper
import com.diniz.repository.ClienteRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/clients")
class ClienteController(
    private val repository: ClienteRepository,
    private val dateHelper: DateHelper
) {

    @GetMapping
    fun findAll(page: Int?, limit: Int?, q: String?): List<ClienteDTO> {
        val ordenacao = Sort.by("name").ascending()
        val pageable = if (page == null || limit == null || limit <= 0) {
            PageRequest.of(0, Int.MAX_VALUE, ordenacao)
        } else {
            PageRequest.of(page-1, limit, ordenacao);
        }

        val page = if (q.isNullOrBlank()) repository.findAll(pageable) else repository.findAllByNameContains(pageable, q)
        return page.content.sortedBy { it.name }.map {
            ClienteDTO.from(it, dateHelper)
        }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ClienteDTO> {
        return repository.findById(id)
            .map { cliente -> ResponseEntity.ok(ClienteDTO.from(cliente, dateHelper)) }
            .orElse(ResponseEntity.notFound().build())
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: ClienteDTO): ClienteDTO {
        if (repository.existsByNameAndPhone(dto.name, dto.phone)) {
            throw RuntimeException("Já existe um cliente com esses dados")
        }
        val novoCliente = Cliente(
            name = dto.name,
            phone = dto.phone,
            email = dto.email,
            birthDate = dateHelper.obtenhaObjetoData_DD_MM_YYYY(dto.birthDate),
            observations = dto.observations
        )
        val created = repository.save(novoCliente)
        return ClienteDTO.from(created, dateHelper)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ClienteDTO): ResponseEntity<ClienteDTO> {
        if (repository.existsByNameAndPhoneAndIdNot(dto.name, dto.phone, id)) {
            throw RuntimeException("Já existe um cliente com esses dados")
        }
        return repository.findById(id).map { existingCliente ->
            existingCliente.name = dto.name
            existingCliente.phone = dto.phone
            existingCliente.email = dto.email
            existingCliente.birthDate = dateHelper.obtenhaObjetoData_DD_MM_YYYY(dto.birthDate)
            existingCliente.observations = dto.observations
            val savedCliente = repository.save(existingCliente)
            ResponseEntity.ok(ClienteDTO.from(savedCliente, dateHelper))
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