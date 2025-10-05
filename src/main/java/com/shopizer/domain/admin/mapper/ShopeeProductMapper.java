package com.shopizer.domain.admin.mapper;

import com.shopizer.domain.admin.dto.AdminShopeeProduct;
import com.shopizer.entities.ShopeeProduct;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopeeProductMapper {

  AdminShopeeProduct toAdminShopeeProductDto(ShopeeProduct entity);

}