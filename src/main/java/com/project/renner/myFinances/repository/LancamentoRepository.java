package com.project.renner.myFinances.repository;

import com.project.renner.myFinances.enums.TipoLancamento;
import com.project.renner.myFinances.model.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query(value = "SELECT sum(l.valor) FROM Lancamento l JOIN l.usuario u" +
            " WHERE u.id = :idUsuario AND l.tipo =:tipo GROUP BY u")
    BigDecimal obterSaldoPorTipoLancamentoUsuario(@Param("idUsuario") Long idUsuario, @Param("tipo") TipoLancamento tipo);
}
