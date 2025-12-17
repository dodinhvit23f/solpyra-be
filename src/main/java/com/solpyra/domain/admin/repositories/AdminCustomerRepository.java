package com.solpyra.domain.admin.repositories;

import com.solpyra.entities.Users;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminCustomerRepository extends JpaRepository<Users, BigInteger> {

}
