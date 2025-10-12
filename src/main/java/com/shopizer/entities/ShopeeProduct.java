package com.shopizer.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "shopee_product")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ShopeeProduct extends LogEntity {

  @Column(name = "product_code")
  private String productCode;

  @Column(name = "product_name")
  private String productName;

  @Column(name = "store_id")
  private String storeId;

  @Column(name = "store_name")
  private String storeName;

  @Column(name = "affiliate_link")
  private String affiliateLink;

  @OneToMany(mappedBy = "product",
      cascade = CascadeType.ALL,
      orphanRemoval = false,
      fetch = FetchType.LAZY)
  private List<ShopeeOrder> orders;
}
