package com.shopizer.domain.admin.services;

import java.math.BigInteger;
import java.util.Map;

public interface AdminCustomerService {

  Map<BigInteger, String> findAllCustomersName();
}
