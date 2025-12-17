package com.solpyra.domain.user.mapper;

import com.solpyra.domain.user.dto.UserShopeeOrder;
import com.solpyra.entities.UserPerShopeeOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserShopeeOrderMapper {


  @Mapping(source = "id", target = "id")
  @Mapping(source = "product.productName", target = "productName")
  @Mapping(source = "order.orderId", target = "orderId")
  @Mapping(source = "product.storeName", target = "storeName")
  @Mapping(source = "order.orderDate", target = "orderDate")
  @Mapping(source = "order.completedDate", target = "completedDate")
  @Mapping(source = "order.commissionedDate", target = "commissionedDate")
  @Mapping(source = "order.userCommission", target = "userCommission")
  @Mapping(source = "order.userCommissionRate", target = "userCommissionRate")
  @Mapping(source = "order.status", target = "status")
  UserShopeeOrder toUserShopeeOrderDto(UserPerShopeeOrder entity);


}