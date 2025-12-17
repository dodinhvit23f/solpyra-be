package com.solpyra.domain.admin.mapper;

import com.solpyra.domain.admin.dto.AdminShopeeProduct;
import com.solpyra.entities.ShopeeProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminShopeeProductMapper {

  AdminShopeeProduct toAdminShopeeProductDto(ShopeeProduct entity);

}