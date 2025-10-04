package com.shopizer.common.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageObject {
    private int page;
    private int totalPage;
    private int pageSize;
    private List<?> list;
}
