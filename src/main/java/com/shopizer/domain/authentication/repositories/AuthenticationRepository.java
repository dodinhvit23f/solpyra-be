package com.shopizer.domain.authentication.repositories;

import com.shopizer.entities.Users;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<Users, BigInteger> {

  @EntityGraph(attributePaths = "role")
  Optional<Users> findByUserNameAndIsDeleted(String userName, Boolean aFalse);

  Optional<Users>  findByUserName(String userName);

}
