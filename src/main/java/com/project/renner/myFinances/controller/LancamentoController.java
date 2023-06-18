package com.project.renner.myFinances.controller;

import com.project.renner.myFinances.dto.AtualizaStatusDTO;
import com.project.renner.myFinances.dto.LancamentoDTO;
import com.project.renner.myFinances.enums.StatusLancamento;
import com.project.renner.myFinances.enums.TipoLancamento;
import com.project.renner.myFinances.exception.RegraNegocioException;
import com.project.renner.myFinances.model.Lancamento;
import com.project.renner.myFinances.model.Usuario;
import com.project.renner.myFinances.service.LancamentoService;
import com.project.renner.myFinances.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {
    //@Autowired
    private final LancamentoService lancamentoService;

    //@Autowired
    private final UsuarioService usuarioService;

//    public LancamentoController(LancamentoService lancamentoService, UsuarioService usuarioService){
//        this.lancamentoService = lancamentoService;
//        this.usuarioService = usuarioService;
//    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO lancamentoDTO){
        try {
            Lancamento lancamento = converter(lancamentoDTO);
            lancamento = lancamentoService.salvar(lancamento);
            return new ResponseEntity(lancamento, HttpStatus.CREATED); //ResponseEntity.ok()
        } catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("{id}")
    public ResponseEntity obterLancamento(@PathVariable("id") Long id){
        return lancamentoService.obterPorId(id)
                .map(lancamento -> new ResponseEntity(converterToDTO(lancamento), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO){
       return lancamentoService.obterPorId(id).map(entity -> {
           try {
               Lancamento lancamento = converter(lancamentoDTO);
               lancamento.setId(entity.getId());
               lancamentoService.atualizar(lancamento);
               return ResponseEntity.ok(lancamento);
           }catch(RegraNegocioException e){
               return ResponseEntity.badRequest().body(e.getMessage());
           }
        }).orElseGet(() -> new ResponseEntity("Lancamento nao encontrado na base de dados.",
               HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id){
        return lancamentoService.obterPorId(id).map(entity ->{
            lancamentoService.deletar(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity( "Lancamento nao encontrado na base de dados.",
                HttpStatus.BAD_REQUEST ));
    }

    @GetMapping
    public ResponseEntity buscar(
            //@RequestParam java.util.Map<String, String> params;
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value =  "usuario") Long idUsuario){
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if(!usuario.isPresent()){
            return ResponseEntity.badRequest().body(" Nao foi possivel realizar a consulta");
        }else{
            lancamentoFiltro.setUsuario(usuario.get());
        }
       List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);

        return ResponseEntity.ok(lancamentos);
    }

    private LancamentoDTO converterToDTO(Lancamento lancamento){
        return LancamentoDTO
                .builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .mes(lancamento.getMes())
                .ano(lancamento.getAno())
                .status(lancamento.getStatus().name())
                .tipo(lancamento.getTipo().name())
                .usuario(lancamento.getUsuario().getId())
                .build();
    }
    private Lancamento converter(LancamentoDTO lancamentoDTO){
        Lancamento lancamento = new Lancamento();
        lancamento.setId(lancamentoDTO.getId());
        lancamento.setDescricao(lancamentoDTO.getDescricao());
        lancamento.setAno(lancamentoDTO.getAno());
        lancamento.setMes(lancamentoDTO.getMes());
        lancamento.setValor(lancamentoDTO.getValor());
        lancamento.setUsuario(usuarioService.
                obterPorId(lancamentoDTO.getUsuario()).orElseThrow
                        (() -> new RegraNegocioException("Usuario nao encontrado com o Id informado.")));
        if(lancamentoDTO.getTipo() != null){
            lancamento.setTipo(TipoLancamento.valueOf(lancamentoDTO.getTipo()));
        }
        if (lancamentoDTO.getStatus() != null){
            lancamento.setStatus(StatusLancamento.valueOf(lancamentoDTO.getStatus()));
        }
        return lancamento;
    }
    @PutMapping("{id}/atualizar-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO statusDTO){
        return lancamentoService.obterPorId(id).map(entity -> {
         StatusLancamento statusSelecionado = StatusLancamento.valueOf(statusDTO.getStatus());

         if(statusSelecionado == null){
             return ResponseEntity.badRequest().body("Nao foi possivel atualizar o status do lancamento, envie um " +
                     "status  valido.");
         }
         try {
             entity.setStatus(statusSelecionado);
             lancamentoService.atualizar(entity);
             return ResponseEntity.ok(entity);
         }catch (RegraNegocioException e){
             return ResponseEntity.badRequest().body(e.getMessage());
         }

        }).orElseGet(() -> new ResponseEntity("Lancamento nao encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
    }
}
