package com.diniz.repository

import com.diniz.domain.Cliente
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClienteRepository : JpaRepository<Cliente, Long> {

    override fun findAll(pageable: Pageable): Page<Cliente>

    fun findAllByNameContains(pageable: Pageable, name: String): Page<Cliente>

    fun existsByNameAndPhone(name: String, phone: String): Boolean

    fun existsByNameAndPhoneAndIdNot(name: String, phone: String, id: Long): Boolean

}