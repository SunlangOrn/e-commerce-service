package com.liang.address.service;

import com.liang.address.dto.AddressRequest;
import com.liang.address.dto.AddressResponse;
import com.liang.address.entity.Address;
import com.liang.shared.metadata.Metadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AddressService {

    Page<AddressResponse> list(Metadata metadata, Pageable pageable);

    AddressResponse view(Metadata metadata, Long addressId);

    AddressResponse create(Metadata metadata, AddressRequest request);

    AddressResponse update(Metadata metadata, AddressRequest request, Long addressId);

    void delete(Metadata metadata ,Long addressId);

    AddressResponse setDefaultAddress(Metadata metadata,Long addressId);

    Page<AddressResponse> adminList(Long userId, Pageable pageable);

    AddressResponse adminView(Long id);
}
