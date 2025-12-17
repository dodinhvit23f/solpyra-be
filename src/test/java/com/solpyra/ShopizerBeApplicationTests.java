package com.solpyra;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;


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

  }

}
