package com.solpyra.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public final class Utils {

  private Utils() {
  }

  private static final Random RANDOM = new Random();


  public static String currencyFormat(Double doubleValue) {
    DecimalFormat formatter = new DecimalFormat("#,###");
    return formatter.format(doubleValue);
  }

  public static String toHour(LocalDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    return formatter.format(date);
  }

  public static Date minusDate(Date date, int calenderOption, int value) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(calenderOption, -value);

    return calendar.getTime();
  }

  public static Date plusDate(Date date, int calenderOption, int value) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(calenderOption, value);

    return calendar.getTime();
  }

  public static String toDate(Date date, String format) {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    return formatter.format(date);
  }

  public static String toDate(LocalDateTime date, String format) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return formatter.format(date);
  }

  public static Date toDate(String date) {
    SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
    try {
      return dt.parse(date);
    } catch (ParseException e) {
      return new Date();
    }
  }

  public static int getTotalPage(int totalRecord, int pageSize) {
    int page = (totalRecord - 1) / pageSize;

    if (page == 0 || totalRecord == 0) {
      return 1;
    }

    return page + 1;
  }

  public static int getOffset(int page, int pageSize) {
    return (page - 1) * pageSize;
  }

  /**
   * Convert Pageable sort to QueryDSL OrderSpecifiers.
   *
   * @param pageable the Pageable containing sorting information
   * @param rootPath the root PathBuilder (usually from your Q-class)
   * @return list of OrderSpecifiers
   */
  public static List<OrderSpecifier<?>> toOrderSpecifiers(Pageable pageable,
      PathBuilder<?> rootPath) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

    if (pageable == null || pageable.getSort().isUnsorted()) {
      return orderSpecifiers;
    }

    for (Sort.Order order : pageable.getSort()) {
      Order direction = order.isAscending() ? Order.ASC : Order.DESC;

      // Handle nested property paths (like "product.productName")
      String[] pathElements = order.getProperty().split("\\.");
      PathBuilder<?> path = rootPath;
      for (String pathElement : pathElements) {
        path = path.get(pathElement);
      }

      orderSpecifiers.add(new OrderSpecifier<>(direction,  Expressions.stringPath(path.toString())));
    }

    return orderSpecifiers;
  }
}
