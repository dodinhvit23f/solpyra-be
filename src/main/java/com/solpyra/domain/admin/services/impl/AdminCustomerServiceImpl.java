package com.solpyra.domain.admin.services.impl;

import com.solpyra.domain.admin.repositories.AdminCustomerRepository;
import com.solpyra.domain.admin.services.AdminCustomerService;
import com.solpyra.entities.Users;
import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCustomerServiceImpl implements AdminCustomerService {

  final AdminCustomerRepository adminCustomerRepository;

  @Override
  public Map<BigInteger, String> findAllCustomersName() {
    return adminCustomerRepository.findAll()
        .stream()
        .collect(Collectors.toMap(Users::getId, Users::getUserName));
  }
}
