package com.liang;

import com.liang.payment.AbaPayWayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableConfigurationProperties(AbaPayWayProperties.class)
@SpringBootApplication
public class EcommerceServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(EcommerceServiceApplication.class, args);
  }
}
