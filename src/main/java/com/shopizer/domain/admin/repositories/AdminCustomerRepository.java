package com.shopizer.domain.admin.repositories;

import com.shopizer.entities.Users;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminCustomerRepository extends JpaRepository<Users, BigInteger> {

}
