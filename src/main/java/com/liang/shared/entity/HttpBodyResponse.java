package com.liang.shared.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpBodyResponse<T> {
  @Builder.Default private Integer status = HttpStatus.OK.value();
  @Builder.Default private String message = "Successful";
  private T data;
  private HttpBodyPagingResponse paging;
  private HttpBodyErrorResponse error;
}
