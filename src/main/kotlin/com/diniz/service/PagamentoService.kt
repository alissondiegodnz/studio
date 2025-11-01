package com.diniz.service

import com.diniz.domain.MetodoPagamento
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

        val servicosParaRemover = mutableListOf<ServicoPagamento>()
        atualizeServicosPagamentoExistente(pagamentoExistente, dto, servicosParaRemover)
        pagamentoExistente.servicosPagamento.removeAll(servicosParaRemover)
        pagamentoExistente.servicosPagamento.addAll(obtenhaNovosServicosPagamentoExistente(dto))

        val metodosPagamentoParaRemover = mutableListOf<MetodoPagamento>()
        atualizeMetodosPagamentoExistente(pagamentoExistente, dto, metodosPagamentoParaRemover)
        pagamentoExistente.metodosPagamento.removeAll(metodosPagamentoParaRemover)
        pagamentoExistente.metodosPagamento.addAll(obtenhaNovosMetodosPagamentoExistente(dto))

        pagamentoExistente.cliente = clienteRepository.getReferenceById(dto.clientId.toLong())
        pagamentoExistente.pacote = if (dto.packageId.isBlank()) null else pacoteRepository.findById(dto.packageId.toLong()).orElse(null)
        pagamentoExistente.data = dateHelper.obtenhaDataHora(dto.date, dto.time)
        pagamentoExistente.descricao = dto.description
        pagamentoExistente.tipoDeServico = dto.serviceType

        garantaRelacoesFilhasPagamento(pagamentoExistente)
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

    private fun atualizeMetodosPagamentoExistente(
        pagamentoExistente: Pagamento,
        dto: PagamentoDTO,
        relacoesParaRemover: MutableList<MetodoPagamento>
    ) {
        pagamentoExistente.metodosPagamento.forEach { metodoPagamentoExistente ->
            val servicoPagamentoDTO = dto.paymentLines.find { it.id?.toLongOrNull() == metodoPagamentoExistente.id }
            if (servicoPagamentoDTO != null) {
                metodoPagamentoExistente.valor = servicoPagamentoDTO.value
            } else {
                relacoesParaRemover.add(metodoPagamentoExistente)
            }
        }
    }

    private fun obtenhaNovosMetodosPagamentoExistente(dto: PagamentoDTO): MutableList<MetodoPagamento> {
        val novosMetodosPagamento = mutableListOf<MetodoPagamento>()
        dto.paymentLines.map {
            if (it.id == null || it.id.toLongOrNull() == null) {
                novosMetodosPagamento.add(MetodoPagamento(
                    metodoPagamento = it.paymentMethod,
                    valor = it.value,
                ))
            }
        }
        return novosMetodosPagamento
    }

    fun crieNovoPagamento(dto: PagamentoDTO) {
        dateHelper.valideDataHora(dto)
        val servicosPagamentos = obtenhaServicosNovoPagamento(dto)
        val metodosPagamento = obtenhaMetodosPagamentoNovoPagamento(dto)

        val novoPagamento = Pagamento(
            cliente = clienteRepository.getReferenceById(dto.clientId.toLong()),
            pacote = if (dto.packageId.isBlank()) null else pacoteRepository.findById(dto.packageId.toLong()).orElse(null),
            servicosPagamento = servicosPagamentos,
            metodosPagamento = metodosPagamento,
            data = dateHelper.obtenhaDataHora(dto.date, dto.time),
            descricao = dto.description,
            tipoDeServico = dto.serviceType
        )
        garantaRelacoesFilhasPagamento(novoPagamento)
        repository.save(novoPagamento)
    }

    private fun garantaRelacoesFilhasPagamento(pagamento: Pagamento) {
        pagamento.servicosPagamento.forEach { it.pagamento = pagamento }
        pagamento.metodosPagamento.forEach { it.pagamento = pagamento }
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

    private fun obtenhaMetodosPagamentoNovoPagamento(dto: PagamentoDTO): MutableList<MetodoPagamento> {
        return dto.paymentLines.map {
            MetodoPagamento(
                metodoPagamento = it.paymentMethod,
                valor = it.value
            )
        } as MutableList
    }

}