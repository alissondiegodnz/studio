package com.diniz.controller

import com.diniz.domain.Servico
import com.diniz.dto.ServicoDTO
import com.diniz.repository.ServicoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/services")
class ServicoController(
    private val repository: ServicoRepository,
) {
    
    @GetMapping
    fun findAllActive(): List<ServicoDTO> {
        return repository.findAll().filter { it.status == "Ativo" }.map { ServicoDTO.from(it) }.sortedBy {
            it.name
        }
    }

    @GetMapping("/allStatus")
    fun findAll(): List<ServicoDTO> {
        return repository.findAll().map { ServicoDTO.from(it) }.sortedBy {
            it.name
        }
    }
    
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ServicoDTO> {
        return repository.findById(id)
            .map { servico -> ResponseEntity.ok( ServicoDTO.from(servico)) }
            .orElse(ResponseEntity.notFound().build())
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: ServicoDTO): ServicoDTO {
        val novoServico = Servico(
            nome = dto.name,
            categoria = dto.category!!,
            status = dto.status!!,
            valor = dto.price,
            descricao = dto.description
        )
        val created = repository.save(novoServico)
        return ServicoDTO.from(created)
    }
    
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ServicoDTO): ResponseEntity<ServicoDTO> {
        return repository.findById(id).map { existingServico ->
            existingServico.nome = dto.name
            existingServico.categoria = dto.category!!
            existingServico.status = dto.status!!
            existingServico.valor = dto.price
            existingServico.descricao = dto.description
            val savedServico = repository.save(existingServico)
            ResponseEntity.ok(ServicoDTO.from(savedServico))
        }.orElse(ResponseEntity.notFound().build())
    }
    
//    @DeleteMapping("/{id}")
//    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
//        return if (repository.existsById(id)) {
//            repository.deleteById(id)
//            ResponseEntity.noContent().build()
//        } else {
//            ResponseEntity.notFound().build()
//        }
//    }
}