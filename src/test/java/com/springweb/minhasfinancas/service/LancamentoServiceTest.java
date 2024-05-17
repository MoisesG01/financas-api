package com.springweb.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.springweb.minhasfinancas.exception.RegraNegocioException;
import com.springweb.minhasfinancas.model.entity.Lancamento;
import com.springweb.minhasfinancas.model.entity.Usuario;
import com.springweb.minhasfinancas.model.enums.StatusLancamento;
import com.springweb.minhasfinancas.model.repository.LancamentoRepository;
import com.springweb.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.springweb.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento() {
        // Cenário
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(Mockito.any(Lancamento.class));

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.when(repository.save(Mockito.any(Lancamento.class))).thenReturn(lancamentoSalvo);

        // Execução
        Lancamento lancamento = service.salvar(lancamentoASalvar);

        // Verificação
        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
        // Cenário
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(Mockito.any(Lancamento.class));

        // Execução e Verificação
        Assertions.assertThatThrownBy(() -> service.salvar(lancamentoASalvar))
                  .isInstanceOf(RegraNegocioException.class);

        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }
    
    @Test
    public void deveAtualizarUmLancamento() {
        // Cenário
    	Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
    	lancamentoSalvo.setId(1l);
    	lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
    	
        Mockito.doNothing().when(service).validar(Mockito.any(Lancamento.class));

        Mockito.when(repository.save(Mockito.any(Lancamento.class))).thenReturn(lancamentoSalvo);

        // Execução
        service.atualizar(lancamentoSalvo);

        // Verificação
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
        
    }
    
    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        // Cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        // Execução e Verificação
        Assertions.assertThatThrownBy(() -> service.atualizar(lancamento))
                  .isInstanceOf(RegraNegocioException.class)
                  .hasMessage("Informe um Usuário válido!");
        Mockito.verify(repository, Mockito.never()).save(lancamento);
    }
    
    @Test
    public void deveDeletarUmLancamento() {
    	// Cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        
        // Execução
        service.deletar(lancamento);
        
        // Verificação
        Mockito.verify(repository).delete(lancamento);
    }
    
    @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
        // Cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        
        // Execução e Verificação
        Assertions.assertThatThrownBy(() -> service.deletar(lancamento))
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessage("ID de lançamento inválido: 0");
        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }
    
    @Test
    public void deveFiltarLancamentos() {
    	// Cenário
    	Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	lancamento.setId(1l);
    	
    	List<Lancamento> lista = Arrays.asList(lancamento);
    	Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
    	
    	// Execução
    	List<Lancamento> resultado = service.buscar(lancamento);
    	
    	// Verificação
    	Assertions.
    		assertThat(resultado)
    		.isNotEmpty()
    		.hasSize(1)
    		.contains(lancamento);
    }
    
    @Test
    public void deveAtualizarOStatusDeUmLancamento() {
    	// Cenário
    	Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	lancamento.setId(1l);
    	lancamento.setStatus(StatusLancamento.PENDENTE);
    	
    	StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
    	Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
    	
    	// Execução
    	service.atualizarStatus(lancamento, novoStatus);
    	
    	// Verificação
    	Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
    	Mockito.verify(service).atualizar(lancamento);
    }
    
    @Test
    public void deveObterUmLancamentoPorId() {
    	// Cenário
    	Long id = 1l;
    	
    	Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	lancamento.setId(id);
    	
    	Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
    	
    	// Execução
    	Optional<Lancamento> resultado = service.obterPorId(id);
    	
    	// Verificação
    	Assertions.assertThat(resultado.isPresent()).isTrue();
    }
    
    @Test
    public void deveRetornarVazioQuandoOLancamentoNaoExistir() {
    	// Cenário
    	Long id = 1l;
    	
    	Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
    	lancamento.setId(id);
    	
    	Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
    	
    	// Execução
    	Optional<Lancamento> resultado = service.obterPorId(id);
    	
    	// Verificação
    	Assertions.assertThat(resultado.isPresent()).isFalse();
    }
    
    @Test
    public void deveLancarErrosAoValidarUmLancamento() {
    	Lancamento lancamento = new Lancamento();
    	
    	Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");
    	
    	lancamento.setDescricao("");
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");
    	
    	lancamento.setDescricao("Salario");
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");
    	
    	lancamento.setAno(0);
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");
    	
    	lancamento.setAno(13);
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");
    	
    	lancamento.setMes(1);
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");
    	
    	lancamento.setAno(202);
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");
    	
    	lancamento.setAno(2020);
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário válido!");
    	
    	lancamento.setUsuario(new Usuario());
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");
    	
    	lancamento.getUsuario().setId(1l);
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");
    	
    	lancamento.setValor(BigDecimal.ZERO);
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");
    	
    	lancamento.setValor(BigDecimal.valueOf(1));
    	
    	erro = Assertions.catchThrowable( () -> service.validar(lancamento));
    	Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lançamento!");
    }

}
