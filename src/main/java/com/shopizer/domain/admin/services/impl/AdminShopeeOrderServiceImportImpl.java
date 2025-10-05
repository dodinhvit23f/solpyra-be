package com.shopizer.domain.admin.services.impl;

import com.shopizer.constant.ApplicationMessage;
import com.shopizer.constant.Constant;
import com.shopizer.constant.OrderStatus;
import com.shopizer.domain.admin.mapper.ShopeeOrderMapper;
import com.shopizer.domain.admin.repositories.AdminShopeeOrderRepository;
import com.shopizer.domain.admin.repositories.AdminShopeeProductRepository;
import com.shopizer.domain.admin.services.AdminShopeeOrderImportService;
import com.shopizer.entities.ShopeeOrder;
import com.shopizer.entities.ShopeeProduct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminShopeeOrderServiceImportImpl implements AdminShopeeOrderImportService {

  private static final double USER_MAX_COMMISSION_RATE = 0.6D;
  private static final long COMMISSION_FOOL_PRICE = 5000L;
  private static final DateTimeFormatter DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final Pattern INVISIBLE_CHARS =
      Pattern.compile("[\\uFEFF\\u200B\\u200C\\u200D\\u2060\\u00A0]");
  public static final int RANGE_RETURN_GOODS = 16;

  final AdminShopeeOrderRepository adminShopeeOrderRepository;
  final AdminShopeeProductRepository adminShopeeProductRepository;
  final ShopeeOrderMapper shopeeOrderMapper;

  @Override
  public void importShopeeOrderByCsvFile(MultipartFile file) {
    validateFileCsv(file);

    log.info("Start handle file {}", file.getOriginalFilename());

    Map<String, ShopeeOrder> importOrders = getShopeeOrderList(file).stream()
        .collect(Collectors.toMap(ShopeeOrder::getOrderId, Function.identity()));

    Map<String, ShopeeOrder> savedOrder = adminShopeeOrderRepository.findListShopeeOrdersByOrders(
            importOrders.keySet())
        .stream()
        .collect(Collectors.toMap(ShopeeOrder::getOrderId, Function.identity()));

    Map<String, ShopeeProduct> products = adminShopeeProductRepository.getListWithCollectionOfProductCodes(
            importOrders.values().stream()
                .map(ShopeeOrder::getProduct)
                .map(ShopeeProduct::getProductCode)
                .collect(Collectors.toSet()))
        .stream()
        .collect(Collectors.toMap(ShopeeProduct::getProductCode, Function.identity()));

    mappingExistedOrder(importOrders, savedOrder);
    mappingProduct(savedOrder, products);

    adminShopeeProductRepository.saveAll(products.values());
    adminShopeeOrderRepository.saveAll(savedOrder.values());

    log.info("Done finish file {}.", file.getOriginalFilename());
  }

  private void validateFileCsv(MultipartFile file) {
    if (file.isEmpty()) {
      log.error("TraceId: {} File {} is empty", MDC.get(Constant.TRACE_ID),
          file.getOriginalFilename());
      throw new IllegalArgumentException(ApplicationMessage.AuthenticationMessage.EMPTY_FILE);
    }

    // Check file extension
    String filename = file.getOriginalFilename();
    if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
      throw new IllegalArgumentException(
          ApplicationMessage.AuthenticationMessage.INVALID_FILE_TYPE);
    }

    // Check MIME type (optional)
    String contentType = file.getContentType();
    if (contentType == null ||
        !(contentType.equals("text/csv") || contentType.equals("application/vnd.ms-excel"))) {
      throw new IllegalArgumentException(
          ApplicationMessage.AuthenticationMessage.INVALID_CONTENT_TYPE);
    }
  }

  private List<ShopeeOrder> getShopeeOrderList(MultipartFile file) {
    List<ShopeeOrder> shopeeOrders = new ArrayList<>();

    try (CSVParser csvParser = cleanAndParse(file.getInputStream())) {
      for (CSVRecord record : csvParser) {

        ShopeeProduct product = ShopeeProduct.builder()
            .productName(record.get("Tên Item"))
            .productCode(record.get("Item id"))
            .storeId(record.get("Shop id"))
            .storeName(record.get("Tên Shop"))
            .affiliateLink("")
            .build();

        String orderId = record.get("ID đơn hàng");
        ZonedDateTime orderDate = parseDate(record.get("Thời Gian Đặt Hàng"));
        ZonedDateTime orderCompletedDate = parseDate(record.get("Thời gian hoàn thành"));
        ZonedDateTime commissionedDate = Objects.nonNull(orderCompletedDate) ?
            orderCompletedDate.plusDays(RANGE_RETURN_GOODS) : null;

        BigDecimal totalCommission = parseDecimal(record.get("Tổng hoa hồng đơn hàng(₫)"));
        BigDecimal userCommission = BigDecimal.ZERO;
        BigDecimal platformCommission = totalCommission;

        BigDecimal commissionRate = parsePercent(record.get("Tỷ lệ sản phẩm hoa hồng Shope"))
            .add(parsePercent(record.get("Tỷ lệ sản phẩm hoa hồng người bán")));
        BigDecimal userCommissionRate = BigDecimal.ZERO;
        BigDecimal platformCommissionRate = commissionRate;

        if (totalCommission.compareTo(BigDecimal.valueOf(COMMISSION_FOOL_PRICE)) > 0) {
          userCommission = totalCommission.multiply( BigDecimal.valueOf(USER_MAX_COMMISSION_RATE));
          platformCommission = totalCommission.subtract(userCommission);
          userCommissionRate = commissionRate.multiply(
              BigDecimal.valueOf(USER_MAX_COMMISSION_RATE));
          platformCommissionRate = commissionRate.subtract(userCommissionRate);
        }

        String status = record.get("Trạng thái đặt hàng");

        ShopeeOrder order = ShopeeOrder.builder()
            .orderId(orderId)
            .orderDate(orderDate)
            .completedDate(orderCompletedDate)
            .commissionedDate(commissionedDate)
            .totalCommission(totalCommission)
            .userCommissionRate(userCommissionRate)
            .platformCommissionRate(platformCommissionRate)
            .commissionRate(commissionRate)
            .userCommission(userCommission)
            .platformCommission(platformCommission)
            .status(convertStatus(status.toLowerCase()))
            .product(product)
            .build();

        shopeeOrders.add(order);
      }
    } catch (
        Exception e) {
      log.error("Error while importing shopee orders from CSV file! {}", e.getMessage(), e);
      throw new RuntimeException("Error parsing CSV data", e);
    }

    return shopeeOrders;
  }

  private void mappingExistedOrder(Map<String, ShopeeOrder> importOrders,
      Map<String, ShopeeOrder> savedOrder) {
    importOrders.forEach((orderId, shopeeOrder) -> {
      if (savedOrder.containsKey(orderId)) {
        shopeeOrderMapper.updateStatus(savedOrder.get(orderId), shopeeOrder);
        return;
      }

      savedOrder.put(orderId, shopeeOrder);
    });
  }

  private void mappingProduct(Map<String, ShopeeOrder> savedOrder,
      Map<String, ShopeeProduct> products) {
    savedOrder.values()
        .forEach(shopeeOrder -> {

          if (Objects.nonNull(shopeeOrder.getProduct().getId())) {
            return;
          }

          String productCode = shopeeOrder.getProduct().getProductCode();

          if (!products.containsKey(productCode)) {
            products.put(productCode, shopeeOrder.getProduct());
            return;
          }

          shopeeOrder.setProduct(products.get(productCode));
        });
  }

  private CSVParser cleanAndParse(InputStream inputStream) throws IOException {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    // Read header line first
    String headerLine = reader.readLine();
    if (headerLine == null) {
      throw new IOException("CSV file is empty");
    }

    // Clean invisible characters
    headerLine = INVISIBLE_CHARS.matcher(headerLine).replaceAll("");

    // Split headers manually (assuming comma separated)
    String[] headers = headerLine.split(",");

    // Clean up each header
    for (int i = 0; i < headers.length; i++) {
      headers[i] = headers[i].trim().replaceAll(INVISIBLE_CHARS.pattern(), "");
    }

    // Continue parsing the rest of the CSV
    return CSVFormat.DEFAULT
        .builder()
        .setHeader(headers)
        .setTrim(true)
        .setIgnoreSurroundingSpaces(true)
        .build()
        .parse(reader);
  }

  private OrderStatus convertStatus(String status) {
    if (status.equals("đang chờ xử lý")) {
      return OrderStatus.IN_PROGRESS;
    }

    if (status.equals("hoàn thành")) {
      return OrderStatus.COMPLETED;
    }

    if (status.contains("huỷ") || status.contains("hoàn")) {
      return OrderStatus.CANCEL;
    }

    return OrderStatus.PENDING;
  }

  private ZonedDateTime parseDate(String value) {
    try {
      if (value == null || value.isBlank()) {
        return null;
      }
      return LocalDateTime.parse(value, DATETIME_FORMATTER).atZone(ZoneId.systemDefault());
    } catch (Exception e) {
      return null;
    }
  }

  private BigDecimal parseDecimal(String value) {
    try {
      if (value == null || value.isBlank()) {
        return BigDecimal.ZERO;
      }
      return new BigDecimal(value.replace(",", "").replace("₫", "").trim());
    } catch (Exception e) {
      return BigDecimal.ZERO;
    }
  }

  private BigDecimal parsePercent(String value) {
    try {
      if (value == null || value.isBlank()) {
        return BigDecimal.ZERO;
      }
      return new BigDecimal(value.replace("%", "").trim());
    } catch (Exception e) {
      return BigDecimal.ZERO;
    }
  }
}
