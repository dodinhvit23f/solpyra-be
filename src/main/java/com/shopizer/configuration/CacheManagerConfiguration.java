package com.shopizer.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheManagerConfiguration {

    @Bean
    public CacheManager cacheManager(CaffeineCacheSpecProperties specProperties) {
        SimpleCacheManager manager = new SimpleCacheManager();

        List<CaffeineCache> caches = specProperties.getSpecs().entrySet().stream()
                .map(entry -> new CaffeineCache(
                        entry.getKey(),
                        Caffeine.from(entry.getValue()).build()
                ))
                .toList();

        manager.setCaches(caches);
        return manager;
    }
}
