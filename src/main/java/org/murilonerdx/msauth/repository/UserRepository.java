package org.murilonerdx.msauth.repository;


import org.murilonerdx.msauth.model.Userixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Userixa, Long> {
    Userixa findByUsername(String username);

    Userixa.Role findRoleByUsername(String username);
}
