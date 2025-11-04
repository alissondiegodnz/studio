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
    
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: PacoteDTO): ResponseEntity<Any> {
        val pacoteExistente = repository.getReferenceById(id)

        val servicosPacoteDTO = dto.services.groupBy { it.id?.toLongOrNull() }
        val servicosParaRemover = mutableListOf<ServicoPacote>()
        pacoteExistente.itens.forEach { servicoPacote ->
            val servicoPagamentoDTO = servicosPacoteDTO.get(servicoPacote.servico.id)?.getOrNull(0)

            if (servicoPagamentoDTO != null) {
                servicoPacote.valor = servicoPagamentoDTO.price ?: servicoPacote.valor
            } else {
                servicosParaRemover.add(servicoPacote)
            }
        }
        pacoteExistente.itens.removeAll(servicosParaRemover)

        val idsServicosPacote = pacoteExistente.itens.map { it.servico.id }.toSet()
        val novosServicosPacote = dto.services.mapNotNull { servicoPacoteDTO ->
            if (servicoPacoteDTO.id == null || servicoPacoteDTO.id.toLongOrNull() !in idsServicosPacote) {
                ServicoPacote(
                    servico = servicoRepository.getReferenceById(servicoPacoteDTO.id!!.toLong()),
                    valor = servicoPacoteDTO.price ?: BigDecimal.ZERO
                )
            } else {
                null
            }
        }
        pacoteExistente.itens.addAll(novosServicosPacote)

        pacoteExistente.nome = dto.name
        pacoteExistente.descricao = dto.description
        pacoteExistente.status = dto.status

        pacoteExistente.itens.forEach { it.pacote = pacoteExistente }
        repository.save(pacoteExistente)
        servicoPacoteRepository.saveAll(pacoteExistente.itens)
        return ResponseEntity.noContent().build()
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