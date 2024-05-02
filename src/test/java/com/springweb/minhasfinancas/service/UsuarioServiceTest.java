package com.springweb.minhasfinancas.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
	
	UsuarioService service;
	
	@MockBean
	UsuarioRepository repository;
	
	@BeforeEach
	public void setUp() {
		service = new UsuarioServiceImpl(repository);
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
	    
	    // Ação e verificação
	    assertThrows(ErroAutenticacao.class, () -> {
	        service.autenticar("email@email.com", "senha");
	    });
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
	    //Cenário
	    String senha = "senha";
	    Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
	    Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
	    
	    //Ação e verificação
	    assertThrows(ErroAutenticacao.class, () -> {
	        service.autenticar("email@email.com", "123");
	    });
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
