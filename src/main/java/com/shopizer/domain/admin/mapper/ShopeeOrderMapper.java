package com.shopizer.domain.admin.mapper;

import com.shopizer.domain.admin.dto.AdminShopeeOrder;
import com.shopizer.entities.ShopeeOrder;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ShopeeOrderMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "totalCommission", ignore = true)
    @Mapping(target = "userCommission", ignore = true)
    @Mapping(target = "platformCommission", ignore = true)
    @Mapping(target = "commissionRate", ignore = true)
    @Mapping(target = "userCommissionRate", ignore = true)
    @Mapping(target = "platformCommissionRate", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateStatus(@MappingTarget ShopeeOrder entity, ShopeeOrder imported);

  @Mapping(target = "productName", source = "product.productName")
  @Mapping(target = "storeName", source = "product.storeName")
  //@Mapping(target = "orderDate", expression = "java(toLocalDateTime(entity.getOrderDate()))")
  //@Mapping(target = "completedDate", expression = "java(toLocalDateTime(entity.getCompletedDate()))")
  //@Mapping(target = "commissionedDate", expression = "java(toLocalDateTime(entity.getCommissionedDate()))")
  AdminShopeeOrder toAdminShopeeOrderDto(ShopeeOrder entity);

  default LocalDateTime toLocalDateTime(ZonedDateTime zdt) {
    return zdt != null ? zdt.toLocalDateTime() : null;
  }

  default ZonedDateTime toZonedDateTime(LocalDateTime ldt) {
    return ldt != null ? ldt.atZone(java.time.ZoneId.systemDefault()) : null;
  }
}