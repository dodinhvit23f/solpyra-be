package com.shopizer.domain.admin.services;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface AdminShopeeOrderImportService {

  @Transactional
  void importShopeeOrderByCsvFile(MultipartFile file);
}
