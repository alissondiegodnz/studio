package com.diniz.controller

import com.diniz.domain.Profissional
import com.diniz.dto.ProfissionalDTO
import com.diniz.repository.ProfissionalRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/professionals")
class ProfissionalController(
    private val repository: ProfissionalRepository,
) {
    
    @GetMapping
    fun findAllActive(): List<ProfissionalDTO> {
        return repository.findAll().filter { it.status == "Ativo" }.map { ProfissionalDTO.from(it) }
    }

    @GetMapping("/allStatus")
    fun findAll(): List<ProfissionalDTO> {
        return repository.findAll().map { ProfissionalDTO.from(it) }
    }
    
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<ProfissionalDTO> {
        return repository.findById(id)
            .map { profissional -> ResponseEntity.ok( ProfissionalDTO.from(profissional)) }
            .orElse(ResponseEntity.notFound().build())
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: ProfissionalDTO): ProfissionalDTO {
        val novoProfissional = Profissional(
            name = dto.name,
            phone = dto.phone,
            status = dto.status,
            address = dto.address
        )
        val created = repository.save(novoProfissional)
        return ProfissionalDTO.from(created)
    }
    
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ProfissionalDTO): ResponseEntity<ProfissionalDTO> {
        return repository.findById(id).map { existingProfissional ->
            existingProfissional.name = dto.name
            existingProfissional.phone = dto.phone
            existingProfissional.status = dto.status
            existingProfissional.address = dto.address
            val savedProfissional = repository.save(existingProfissional)
            ResponseEntity.ok(ProfissionalDTO.from(savedProfissional))
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