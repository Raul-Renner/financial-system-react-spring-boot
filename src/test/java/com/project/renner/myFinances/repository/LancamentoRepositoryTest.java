package com.project.renner.myFinances.repository;

import com.project.renner.myFinances.enums.StatusLancamento;
import com.project.renner.myFinances.enums.TipoLancamento;
import com.project.renner.myFinances.model.Lancamento;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository lancamentoRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamento = Lancamento.builder()
                .ano(2019)
                .mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now()).build();
        lancamento = lancamentoRepository.save(lancamento);

        Assertions.assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    public void DeveDeletarUmLancamento(){
        Lancamento lancamento = criarEPersistirLancamento();
        entityManager.persist(lancamento);
        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        lancamentoRepository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        Assertions.assertThat(lancamentoInexistente).isNull();
    }

    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamento = criarEPersistirLancamento();
        lancamento.setAno(2018);
        lancamento.setDescricao("teste atualizar!");
        lancamento.setStatus(StatusLancamento.CANCELADO);
        lancamentoRepository.save(lancamento);

       Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

       Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
       Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("teste atualizar!");
       Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
    }

    @Test
    public void deveBuscarLancamentoPorId(){
       Lancamento lancamento = criarEPersistirLancamento();

       Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(lancamento.getId());

       Assertions.assertThat(lancamentoEncontrado.isPresent()).isTrue();

    }

    private Lancamento criarEPersistirLancamento(){
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }
    public static Lancamento criarLancamento(){
        return Lancamento.builder()
                .ano(2019).mes(1)
                .descricao("lancamento v2")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }
}
