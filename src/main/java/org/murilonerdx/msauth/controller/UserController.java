package org.murilonerdx.msauth.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murilonerdx.msauth.dto.PasswordChangeDTO;
import org.murilonerdx.msauth.dto.UserCreateDTO;
import org.murilonerdx.msauth.dto.UserResponseDTO;
import org.murilonerdx.msauth.exception.NotFoundException;
import org.murilonerdx.msauth.exception.UsernameExistException;
import org.murilonerdx.msauth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService usuarioService;

    @PostMapping()
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserCreateDTO usuario) throws UsernameExistException {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(usuario));
    }

    @Operation(summary = "Listar usuarios", description = "Recurso para listar usuarios",
            security = @SecurityRequirement(name = "security_auth")
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getAll() throws UsernameExistException {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.findAll());
    }

    @Operation(summary = "Criar um novo cliente", description = "Recurso para criar um novo cliente",
            security = @SecurityRequirement(name = "security_auth")
    )
    @PatchMapping("recovery/password/{id}")
    public ResponseEntity<UserResponseDTO> updatePassword(@PathVariable("id") Long id, @RequestBody PasswordChangeDTO passwordChangeDTO) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.updatePassword(id, passwordChangeDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar usuario soft delete", description = "Não é possivel deletar um usuario completamente.",
            security = @SecurityRequirement(name = "security_auth")
    )
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteAccount(@RequestHeader String token) throws NotFoundException {
        usuarioService.deleteAccount(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("active/{id}")
    public ResponseEntity<Void> activeAccount(@PathVariable("id") String code, @RequestHeader String token) throws NotFoundException {
        usuarioService.deleteAccount(token);
        return ResponseEntity.noContent().build();
    }


}
