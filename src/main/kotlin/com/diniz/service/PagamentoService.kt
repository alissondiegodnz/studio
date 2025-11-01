package com.diniz.service

import com.diniz.domain.Pagamento
import com.diniz.domain.ServicoPagamento
import com.diniz.dto.PagamentoDTO
import com.diniz.helper.DateHelper
import com.diniz.repository.*
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PagamentoService(
    private val repository: PagamentoRepository,
    private val clienteRepository: ClienteRepository,
    private val pacoteRepository: PacoteRepository,
    private val servicoRepository: ServicoRepository,
    private val profissionalRepository: ProfissionalRepository,
    private val dateHelper: DateHelper
) {

    fun obtenhaPagamento(id: Long): PagamentoDTO? {
        val pagamento = repository.findById(id)
        if (pagamento.isEmpty) {
            return null
        }
        val valorTotal = pagamento.get().servicosPagamento.sumOf { it.valor }
        return PagamentoDTO.from(pagamento.get(), valorTotal, false)
    }

    fun removaPagamento(id: Long): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun obtenhaPagamentos(
        startDate: String?,
        endDate: String?,
        category: String?,
        professionalId: String?
    ): List<PagamentoDTO> {
        val paramStartDate = dateHelper.obtenhaDataHoraInicioDoDia(startDate)
        val paramEndDate = dateHelper.obtenhaDataHoraFimDoDia(endDate)
        val pagamentos = repository.findAllByFilters(
            paramStartDate,
            paramEndDate,
            category,
            if (professionalId.isNullOrBlank()) null else professionalId.toLong()
        )

        val possuiFiltroCategoria = !category.isNullOrBlank()
        val possuiFiltroProfissional = !professionalId.isNullOrBlank()
        return pagamentos.map { pagamento ->
            var valorTotal = BigDecimal.ZERO
            val valorFiltrado = pagamento.servicosPagamento.sumOf {
                valorTotal += it.valor
                if (possuiFiltroCategoria) {
                    if (it.servico.categoria != category) {
                        return@sumOf BigDecimal.ZERO
                    }
                }
                if (possuiFiltroProfissional) {
                    if (it.profissional.id != professionalId?.toLong()) {
                        return@sumOf BigDecimal.ZERO
                    }
                }
                it.valor
            }
            PagamentoDTO.from(pagamento, valorFiltrado, valorTotal != valorFiltrado)
        }
    }

    fun atualizePagamento(id: Long, dto: PagamentoDTO): Pagamento? {
        dateHelper.valideDataHora(dto)
        val pagamentoExistente = repository.findById(id).orElse(null) ?: return null
        val relacoesParaRemover = mutableListOf<ServicoPagamento>()
        atualizeServicosPagamentoExistente(pagamentoExistente, dto, relacoesParaRemover)
        pagamentoExistente.servicosPagamento.removeAll(relacoesParaRemover)
        pagamentoExistente.servicosPagamento.addAll(obtenhaNovosServicosPagamentoExistente(dto))

        pagamentoExistente.cliente = clienteRepository.getReferenceById(dto.clientId.toLong())
        pagamentoExistente.pacote = if (dto.packageId.isBlank()) null else pacoteRepository.findById(dto.packageId.toLong()).orElse(null)
        pagamentoExistente.metodoPagamento = dto.paymentMethod
        pagamentoExistente.data = dateHelper.obtenhaDataHora(dto.date, dto.time)
        pagamentoExistente.descricao = dto.description
        pagamentoExistente.tipoDeServico = dto.serviceType
        garantaRelacaoServicoPagamento(pagamentoExistente)
        return repository.save(pagamentoExistente)
    }

    private fun atualizeServicosPagamentoExistente(
        pagamentoExistente: Pagamento,
        dto: PagamentoDTO,
        relacoesParaRemover: MutableList<ServicoPagamento>
    ) {
        pagamentoExistente.servicosPagamento.forEach { servicoPagamentoExistente ->
            val servicoPagamentoDTO = dto.serviceLines.find { it.id?.toLongOrNull() == servicoPagamentoExistente.id }
            if (servicoPagamentoDTO != null) {
                servicoPagamentoExistente.valor = servicoPagamentoDTO.value
                if (servicoPagamentoDTO.professionalId.toLong() != servicoPagamentoExistente.profissional.id) {
                    servicoPagamentoExistente.profissional =
                        profissionalRepository.getReferenceById(servicoPagamentoDTO.professionalId.toLong())
                }
            } else {
                relacoesParaRemover.add(servicoPagamentoExistente)
            }
        }
    }

    private fun obtenhaNovosServicosPagamentoExistente(dto: PagamentoDTO): MutableList<ServicoPagamento> {
        val novosServicosPagamento = mutableListOf<ServicoPagamento>()
        dto.serviceLines.map {
            if (it.id == null || it.id.toLongOrNull() == null) {
                novosServicosPagamento.add(ServicoPagamento(
                    servico = servicoRepository.getReferenceById(it.serviceId.toLong()),
                    profissional = profissionalRepository.getReferenceById(it.professionalId.toLong()),
                    valor = it.value,
                    ehPagamentoPacote = it.isPackageService
                ))
            }
        }
        return novosServicosPagamento
    }

    fun crieNovoPagamento(dto: PagamentoDTO) {
        dateHelper.valideDataHora(dto)
        val servicosPagamentos = obtenhaServicosNovoPagamento(dto)

        val novoPagamento = Pagamento(
            cliente = clienteRepository.getReferenceById(dto.clientId.toLong()),
            pacote = if (dto.packageId.isBlank()) null else pacoteRepository.findById(dto.packageId.toLong()).orElse(null),
            servicosPagamento = servicosPagamentos,
            metodoPagamento = dto.paymentMethod,
            data = dateHelper.obtenhaDataHora(dto.date, dto.time),
            descricao = dto.description,
            tipoDeServico = dto.serviceType
        )
        garantaRelacaoServicoPagamento(novoPagamento)
        repository.save(novoPagamento)
    }

    private fun garantaRelacaoServicoPagamento(pagamento: Pagamento) {
        pagamento.servicosPagamento.forEach { it.pagamento = pagamento }
    }

    private fun obtenhaServicosNovoPagamento(dto: PagamentoDTO): MutableList<ServicoPagamento> {
        return dto.serviceLines.map {
            ServicoPagamento(
                servico = servicoRepository.getReferenceById(it.serviceId.toLong()),
                profissional = profissionalRepository.getReferenceById(it.professionalId.toLong()),
                valor = it.value,
                ehPagamentoPacote = it.isPackageService
            )
        } as MutableList
    }

}