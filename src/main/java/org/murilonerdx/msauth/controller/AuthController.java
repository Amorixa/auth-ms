package org.murilonerdx.msauth.controller;


import lombok.extern.slf4j.Slf4j;
import org.murilonerdx.msauth.dto.UserRequestDTO;
import org.murilonerdx.msauth.jwt.JwtService;
import org.murilonerdx.msauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
@Slf4j
public class AuthController {
    private final UserService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userDetailsService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestBody UserRequestDTO dto) throws Exception {
        log.info("Processo de autenticação pelo login {}", dto.username());
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
            var user = userDetailsService.loadUserByUsername(dto.username());

            var jwt = JwtService.createToken(user.getUsername(), user.getAuthorities().stream().findFirst().get().toString());
            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException ex) {
            log.warn("Bad Credentials from username '{}'", dto.username());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).body("Bad Credentials from username "+ dto.username());
        }

    }

}
