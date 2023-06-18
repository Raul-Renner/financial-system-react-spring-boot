package com.project.renner.myFinances.repository;

import com.project.renner.myFinances.model.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void verificarExistenciaDeUmEmail(){
        //cenario
        Usuario usuario = criarUsuario();
        testEntityManager.persist(usuario);
        //acao/execucao
       boolean result = repository.existsByEmail("usuario@email.com");
        //verificacao
        Assertions.assertThat(result).isTrue();
    }
    @Test
    public void retornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail(){
        //cenario
        //acao
        boolean result = repository.existsByEmail("usuario@email.com");

        //verificacao
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados(){
        //cenario
        Usuario usuario = criarUsuario();
        //acao
        Usuario usuarioSalvo = repository.save(usuario);
        //verificacao
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    public void deveBuscarUmUsuarioPorEmail(){
        //cenario
        Usuario usuario = criarUsuario();
        testEntityManager.persist(usuario);
        //verificacao
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void devRetornarVazioAoBuscarUsuarioPorEmailQuandoExisteNaBase(){
        //cenario
        //verificacao
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        Assertions.assertThat(result.isPresent()).isFalse();
    }

    public static Usuario criarUsuario(){
        return Usuario.builder().nome("usuario").email("usuario@email.com").senha("senha").build();

    }

}
