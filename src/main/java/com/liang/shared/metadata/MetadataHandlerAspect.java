package com.liang.shared.metadata;

import com.liang.shared.security.UserPrincipal;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetadataHandlerAspect {
  @Pointcut("@annotation(com.liang.shared.metadata.MetadataHandler)")
  public void metadataHandler() {}

  @Before("metadataHandler() && args(metadata,..)")
  public void handle(Metadata metadata) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!(principal instanceof UserPrincipal userPrincipal)) {
      throw new IllegalArgumentException("Authentication metadata is missing");
    }
    metadata.setUserId(userPrincipal.getId());
    metadata.setEmail(userPrincipal.getUsername());
    metadata.setRole(userPrincipal.getUser().getRole());
  }
}
