package com.shopizer.configuration;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "caching.caffeine")
public class CaffeineCacheSpecProperties {
    private Map<String, String> specs = new HashMap<>();
}
