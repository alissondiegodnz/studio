package com.diniz.controller

import com.diniz.domain.Pacote
import com.diniz.domain.ServicoPacote
import com.diniz.dto.PacoteDTO
import com.diniz.repository.PacoteRepository
import com.diniz.repository.ServicoPacoteRepository
import com.diniz.repository.ServicoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/packages")
class PacoteController(
    private val repository: PacoteRepository,
    private val servicoRepository: ServicoRepository,
    private val servicoPacoteRepository: ServicoPacoteRepository
) {
    
    @GetMapping
    fun findAllActive(): List<PacoteDTO> {
        return repository.findAll().filter { it.status == "Ativo" }.map { PacoteDTO.from(it) }
    }

    @GetMapping("/allStatus")
    fun findAll(): List<PacoteDTO> {
        return repository.findAll().map { PacoteDTO.from(it) }
    }
    
//    @GetMapping("/{id}")
//    fun findById(@PathVariable id: Long): ResponseEntity<PacoteDTO> {
//        return repository.findById(id)
//            .map { pacote -> ResponseEntity.ok( PacoteDTO.from(pacote)) }
//            .orElse(ResponseEntity.notFound().build())
//    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody dto: PacoteDTO): ResponseEntity<Any> {
        val servicosPacote = dto.services.map {
            ServicoPacote(
                servico = servicoRepository.getReferenceById(it.id!!.toLong()),
                valor = it.price ?: BigDecimal.ZERO
            )
        }
        val novoPacote = Pacote(
            nome = dto.name,
            status = dto.status,
            descricao = dto.description
        )
        servicosPacote.forEach { it.pacote = novoPacote }
        repository.save(novoPacote)
        servicoPacoteRepository.saveAll(servicosPacote)
        return ResponseEntity.noContent().build()
    }
    
//    @PutMapping("/{id}")
//    fun update(@PathVariable id: Long, @RequestBody dto: PacoteDTO): ResponseEntity<PacoteDTO> {
//        return repository.findById(id).map { existingPacote ->
//            existingPacote.nome = dto.name
//            existingPacote.categoria = dto.category
//            existingPacote.status = dto.status
//            existingPacote.valor = dto.price
//            existingPacote.descricao = dto.description
//            val savedPacote = repository.save(existingPacote)
//            ResponseEntity.ok(PacoteDTO.from(savedPacote))
//        }.orElse(ResponseEntity.notFound().build())
//    }
    
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any> {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
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