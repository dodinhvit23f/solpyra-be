package com.shopizer;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


class ShopizerBeApplicationTests {

  @Test
  void contextLoads() {
    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    config.setPassword("1d%S>8$H6gu5");
    config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
    config.setPoolSize(1);
    config.setKeyObtentionIterations("1000");
    config.setProviderName("SunJCE");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
    config.setStringOutputType("base64");
    encryptor.setConfig(config);


    System.out.println("host " + encryptor.encrypt("D9ZbWkB9RM9gVhV7L79e7qAoFq0GZk3lU5Dd58aKJxZ=") );
    System.out.println("database " + encryptor.encrypt("rabbitmq4.0") );
    System.out.println("schema " + encryptor.encrypt("postgres17") );
  }

}
