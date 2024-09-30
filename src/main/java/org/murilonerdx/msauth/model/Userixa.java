package org.murilonerdx.msauth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.murilonerdx.msauth.dto.UserResponseDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_user")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Userixa implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;
    private LocalDateTime dateCreated = LocalDateTime.now();
    private LocalDateTime dataUpdated;
    private boolean softDeleted = false;

    public Userixa(Long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum Role{
        ROLE_ADMIN, ROLE_USER
    }

    public UserResponseDTO toDTO(){
        return new UserResponseDTO(id, username);
    }

    public Userixa(String username, String password) {
        this.username = username;
        this.password = password;
        this.dataUpdated = LocalDateTime.now();
    }
}
