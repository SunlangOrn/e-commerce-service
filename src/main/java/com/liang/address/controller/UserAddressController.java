package com.liang.address.controller;


import com.liang.address.dto.AddressRequest;
import com.liang.address.dto.AddressResponse;
import com.liang.address.service.AddressService;
import com.liang.shared.entity.HttpBodyResponse;
import com.liang.shared.metadata.Metadata;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.liang.shared.api.ControllerHandler.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
public class UserAddressController {

    private final AddressService service;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<Page<AddressResponse>>> list(
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return responseSucceed(service.list(new Metadata(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<AddressResponse>> view(@PathVariable Long id) {
        return responseSucceed(service.view(new Metadata(), id));
    }

    @PostMapping
    public ResponseEntity<HttpBodyResponse<AddressResponse>> create(
            @Valid @RequestBody AddressRequest request) {
        return responseCreated(service.create(new Metadata(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<AddressResponse>> update(
            @PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return responseSucceed(service.update(new Metadata(),request ,id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(new Metadata(), id);
        return responseDeleted();
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<HttpBodyResponse<AddressResponse>> setDefault(@PathVariable Long id) {
        return responseSucceed(service.setDefaultAddress(new Metadata(), id));
    }
}
