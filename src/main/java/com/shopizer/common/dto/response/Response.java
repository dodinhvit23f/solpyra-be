package com.shopizer.common.dto.response;

import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private String traceId;
    private Set<String> errorCodes;
    private Map<String,String> extraMessage;
    private T data;
}
