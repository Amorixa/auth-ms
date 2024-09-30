package org.murilonerdx.msauth.service;


import org.murilonerdx.msauth.dto.PasswordChangeDTO;
import org.murilonerdx.msauth.dto.UserCreateDTO;
import org.murilonerdx.msauth.dto.UserResponseDTO;
import org.murilonerdx.msauth.exception.NotFoundException;
import org.murilonerdx.msauth.exception.UsernameExistException;
import org.murilonerdx.msauth.jwt.JwtService;
import org.murilonerdx.msauth.model.Userixa;
import org.murilonerdx.msauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetailsService {
    private final UserRepository usuarioRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO save(UserCreateDTO ud) throws UsernameExistException {
        Userixa byUsername = usuarioRepository.findByUsername(ud.username());
        if (byUsername != null) {
            throw new UsernameExistException("Username já existe");
        }
        return usuarioRepository.save(new Userixa(ud.username(), passwordEncoder.encode(ud.password()))).toDTO();
    }

    @Transactional(readOnly = true)
    public Userixa findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Userixa.Role findRoleByUsername(String username) {
        return usuarioRepository.findRoleByUsername(username);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) throws NotFoundException {
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario não existe")).toDTO();
    }

    public UserResponseDTO updatePassword(Long id, PasswordChangeDTO passwordChangeDTO) throws NotFoundException {
        Userixa usuario = usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario não existe"));

        if (passwordChangeDTO.confirmPassword().equals(passwordChangeDTO.password())) {
            if (passwordEncoder.matches(passwordChangeDTO.password(), usuario.getPassword())) {
                usuario.setPassword(passwordEncoder.encode(passwordChangeDTO.password()));
                return usuarioRepository.save(usuario).toDTO();
            } else {
                throw new RuntimeException(String.format("A senha para %s está incorreta", usuario.getUsername()));
            }
        } else {
            throw new RuntimeException("As senha não coincidem");
        }
    }

    public Userixa accountActive(String username) throws Exception {
        Userixa userixa = usuarioRepository.findByUsername(username);
        if (userixa != null) {
            userixa.setSoftDeleted(false);
            return userixa;
        } else {
            throw new Exception("Usuario não encontrado");
        }
    }

    public void deleteAccount(String token) throws NotFoundException {
        Userixa usuario = usuarioRepository.findByUsername(JwtService.getUsernameFromToken(token));
        if (usuario == null) {
            throw new RuntimeException(String.format("Usuario %s não encontrado", JwtService.getUsernameFromToken(token)));
        } else {
            usuario.setSoftDeleted(true);
            usuarioRepository.save(usuario);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

    public List<UserResponseDTO> findAll() {
        return usuarioRepository.findAll().stream().map(Userixa::toDTO).collect(Collectors.toList());
    }
}
