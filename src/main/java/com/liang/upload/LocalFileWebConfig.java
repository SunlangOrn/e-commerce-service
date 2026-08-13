package com.liang.upload;

import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LocalFileWebConfig implements WebMvcConfigurer {
  @Value("${file-upload.local-dir:uploads}")
  private String localDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String uploadLocation = Paths.get(localDir).toAbsolutePath().normalize().toUri().toString();
    registry.addResourceHandler("/uploads/**").addResourceLocations(uploadLocation);
  }
}
