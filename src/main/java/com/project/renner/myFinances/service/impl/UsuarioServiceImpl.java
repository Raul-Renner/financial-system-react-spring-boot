package com.project.renner.myFinances.service.impl;

import com.project.renner.myFinances.exception.ErroAutenticacao;
import com.project.renner.myFinances.exception.RegraNegocioException;
import com.project.renner.myFinances.model.Usuario;
import com.project.renner.myFinances.repository.UsuarioRepository;
import com.project.renner.myFinances.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if(!usuario.isPresent()){
            throw new ErroAutenticacao("Email nao cadastrado.");
        }
        if(!usuario.get().getSenha().equals(senha)){
            throw new ErroAutenticacao("Senha invalida.");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean exist = usuarioRepository.existsByEmail(email);
        if(exist){
            throw new RegraNegocioException("Já existe um usuário cadastro com esse email");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return usuarioRepository.findById(id);
    }

}
