package com.solpyra.domain.authentication.repositories;

import com.solpyra.entities.Role;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRoleRepository extends JpaRepository<Role, BigInteger> {

    Optional<Role> findByName(String roleName);
}
