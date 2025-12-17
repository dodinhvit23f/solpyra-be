package com.solpyra.domain.user.repositories;

import com.solpyra.entities.Users;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, BigInteger> {

  Users getByUserName(String userName);
}
