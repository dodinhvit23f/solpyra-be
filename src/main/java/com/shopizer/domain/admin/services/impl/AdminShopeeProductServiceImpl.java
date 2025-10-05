package com.shopizer.domain.admin.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.shopizer.common.dto.response.PageObject;
import com.shopizer.constant.ApplicationMessage.AuthenticationMessage;
import com.shopizer.domain.admin.dto.request.AdminProductListRequest;
import com.shopizer.domain.admin.dto.request.AdminUpdateAffiliateLinkRequest;
import com.shopizer.domain.admin.mapper.ShopeeProductMapper;
import com.shopizer.domain.admin.repositories.AdminShopeeProductRepository;
import com.shopizer.domain.admin.services.AdminShopeeProductService;
import com.shopizer.entities.QShopeeProduct;
import com.shopizer.entities.ShopeeProduct;
import com.shopizer.exception.NotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class AdminShopeeProductServiceImpl implements AdminShopeeProductService {

  final AdminShopeeProductRepository shopeeProductRepository;
  final ShopeeProductMapper shopeeProductMapper;

  @Override
  public void updateAffiliateLink(AdminUpdateAffiliateLinkRequest request)
      throws NotFoundException {

    ShopeeProduct shopeeProduct = shopeeProductRepository.findShopeeProductById(request.getId())
        .orElseThrow(() -> new NotFoundException(AuthenticationMessage.PRODUCT_NOT_EXIST));

    shopeeProduct.setAffiliateLink(request.getAffiliateLink());
    shopeeProductRepository.save(shopeeProduct);
  }


  @Override
  public PageObject getProducts(AdminProductListRequest request){
    Page<ShopeeProduct> products = null;

    QShopeeProduct product =  QShopeeProduct.shopeeProduct;

    BooleanExpression search = product.id.isNotNull();

    if(Objects.nonNull(request.getIsNullAffiliate())){
      if(request.getIsNullAffiliate()){
        search = search.and(product.affiliateLink.isEmpty());
      }

      if(!request.getIsNullAffiliate()){
        search = search.and(product.affiliateLink.isNotEmpty());
      }
    }

    if(!ObjectUtils.isEmpty(request.getSearch())){
      String searchValue = request.getSearch();
      search = search.and(product.productName.containsIgnoreCase(searchValue)
          .or(product.storeName.containsIgnoreCase(searchValue)));
    }

    products = shopeeProductRepository.findAll(search, request.getPageable());

    return PageObject.builder()
        .page(request.getPageable().getPageNumber())
        .pageSize(request.getPageable().getPageSize())
        .list(products.getContent()
            .stream()
            .map(shopeeProductMapper::toAdminShopeeProductDto)
            .toList())
        .totalPage(products.getTotalPages())
        .build();
  }
}
