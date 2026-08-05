package com.liang.address.controller;

import com.liang.address.dto.AddressResponse;
import com.liang.address.service.AddressService;
import com.liang.shared.entity.HttpBodyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.liang.shared.api.ControllerHandler.responseSucceed;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/v1/addresses")
public class AdminAddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<HttpBodyResponse<Page<AddressResponse>>> adminList(
            @RequestParam(required = false) Long userId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return responseSucceed(addressService.adminList(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpBodyResponse<AddressResponse>> adminView(@PathVariable Long id) {
        return responseSucceed(addressService.adminView(id));
    }
}
