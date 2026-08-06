package com.liang.shared.api;

import com.liang.shared.entity.HttpBodyPagingResponse;
import com.liang.shared.entity.HttpBodyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControllerHandler {
  private ControllerHandler() {}

  public static <T> ResponseEntity<HttpBodyResponse<T>> responseSucceed(T data) {
    return response(HttpStatus.OK, HttpStatus.OK.name(), data);
  }

  public static <T> ResponseEntity<HttpBodyResponse<T>> responseCreated(T data) {
    return response(HttpStatus.CREATED, HttpStatus.CREATED.name(), data);
  }

  public static <T> ResponseEntity<HttpBodyResponse<T>> responsePaging(
      T data, HttpBodyPagingResponse paging) {
    return ResponseEntity.ok(
        HttpBodyResponse.<T>builder()
            .status(HttpStatus.OK.value())
            .message(HttpStatus.OK.name())
            .data(data)
            .paging(paging)
            .build());
  }

  public static ResponseEntity<Void> responseDeleted() {
    return ResponseEntity.noContent().build();
  }

  public static ResponseEntity<Void> responseOK() {
      return ResponseEntity.ok().build();
  }

  public static <T> ResponseEntity<HttpBodyResponse<T>> response(
      HttpStatus status, String message, T data) {
    return ResponseEntity.status(status)
        .body(
            HttpBodyResponse.<T>builder()
                .status(status.value())
                .message(message)
                .data(data)
                .build());
  }
}
