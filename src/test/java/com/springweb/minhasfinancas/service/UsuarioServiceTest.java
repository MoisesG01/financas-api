package com.springweb.minhasfinancas.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.springweb.minhasfinancas.exception.ErroAutenticacao;
import com.springweb.minhasfinancas.exception.RegraNegocioException;
import com.springweb.minhasfinancas.model.entity.Usuario;
import com.springweb.minhasfinancas.model.repository.UsuarioRepository;
import com.springweb.minhasfinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() {
	    //Cenário
	    Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
	    Usuario usuario = Usuario.builder()
	                .id(1l)
	                .nome("nome")
	                .email("email@email.com")
	                .senha("senha").build();
	    
	    Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
	    
	    //Ação e verificação
	    assertDoesNotThrow(() -> {
	        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
	        
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	    });
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
	    //Cenário
	    String email = "email@email.com";
	    Usuario usuario = Usuario.builder().email(email).build();
	    Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
	    
	    //Ação e verificação
	    assertThrows(RegraNegocioException.class, () -> {
	        service.salvarUsuario(usuario);
	    });

	    Mockito.verify(repository, Mockito.never()).save(usuario);
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
	    //Cenário
	    String email = "email@email.com";
	    String senha = "senha";
	    
	    Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
	    Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
	    
	    //Ação e verificação
	    assertDoesNotThrow(() -> {
	        Usuario result = service.autenticar(email, senha);
	        Assertions.assertThat(result).isNotNull();
	    });
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
	    // Cenário
	    Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
	    
	    // Ação
	    Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "senha") );
	    
	    //Verificação
	    Assertions.assertThat(exception)
	    	.isInstanceOf(ErroAutenticacao.class)
	    	.hasMessage("Usuário Não Encontrado para o Email Informado!");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
	    //Cenário
	    String senha = "senha";
	    Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
	    Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
	    
	    //Ação e verificação
	    Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "123") );
	    Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha Inválida!");
	    
	}
	
	@Test
    public void deveValidarEmail() {
        // Cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        
        // Ação e Verificação
        assertDoesNotThrow(() -> service.validarEmail("email@email.com"));
    }
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailcadastrado() {
	    // Cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
	    
	    // Ação
	    assertThrows(RegraNegocioException.class, () -> {
	        service.validarEmail("email@email.com");
	    });
	}

}
