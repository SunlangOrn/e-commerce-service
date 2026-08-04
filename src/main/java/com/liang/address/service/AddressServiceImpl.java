package com.liang.address.service;

import com.liang.address.dto.AddressRequest;
import com.liang.address.dto.AddressResponse;
import com.liang.address.entity.Address;
import com.liang.address.mapper.AddressMapper;
import com.liang.address.repository.AddressRepository;
import com.liang.shared.api.NotFoundException;
import com.liang.shared.metadata.Metadata;
import com.liang.shared.metadata.MetadataHandler;
import com.liang.shared.security.User;
import com.liang.shared.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper mapper;
    private final UserRepository userRepository;


    @Override
    @MetadataHandler
    public Page<AddressResponse> list(Metadata metadata, Pageable pageable) {
        return addressRepository.findByUserId(metadata.getUserId(), pageable)
                .map(mapper::fromAddress);
    }

    @Override
    @MetadataHandler
    public AddressResponse view(Metadata metadata, Long addressId) {
        Address address = findAddressOrThrow(metadata.getUserId() , addressId );
        return mapper.fromAddress(address);
    }

    @Override
    @MetadataHandler
    @Transactional
    public AddressResponse create(Metadata metadata, AddressRequest request) {
        Long userId = metadata.getUserId();

        // 1. Get a JPA entity proxy without querying the DB for User
        User user = userRepository.getReferenceById(userId);

        // 2. Fast EXISTS query instead of loading all address entities into memory
        boolean hasExistingAddresses = addressRepository.existsByUserId(userId);

        Address address = mapper.fromAddressRequest(request);
        address.setUser(user);

        if (!hasExistingAddresses) {
            // First address is automatically default
            address.setIsDefault(true);
        } else if (Boolean.TRUE.equals(request.getIsDefault())) {
            // Un-default existing addresses first if user explicitly set this new one as default
            addressRepository.resetDefaultAddressForUser(userId);
            address.setIsDefault(true);
        } else {
            address.setIsDefault(false);
        }

        Address saved = addressRepository.save(address);
        return mapper.fromAddress(saved);
    }

    @Override
    @MetadataHandler
    @Transactional
    public AddressResponse update(Metadata metadata, AddressRequest request, Long addressId) {

        Long userId = metadata.getUserId();
        Address address = findAddressOrThrow(userId, addressId);

        // Only trigger bulk UPDATE query if changing from non-default to default
        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.resetDefaultAddressForUser(userId);
        }

        mapper.updateAddressFromRequest(request, address);
        return mapper.fromAddress(address);
    }

    @Override
    @MetadataHandler
    @Transactional
    public void delete(Metadata metadata, Long addressId) {
        Address address = findAddressOrThrow(metadata.getUserId(), addressId );
        addressRepository.delete(address);
    }

    @Override
    @MetadataHandler
    @Transactional
    public AddressResponse setDefaultAddress(Metadata metadata, Long addressId) {

        Long userId = metadata.getUserId();
        Address address = findAddressOrThrow(userId, addressId);

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.resetDefaultAddressForUser(userId);
            address.setIsDefault(true);
        }
        return mapper.fromAddress(address);
    }

    @Override
    public Page<AddressResponse> adminList(Long userId, Pageable pageable) {
        if (userId != null) {
            return addressRepository.findByUserId(userId, pageable)
                    .map(mapper::fromAddress);
        }
        return addressRepository.findAll(pageable)
                .map(mapper::fromAddress);
    }

    @Override
    public AddressResponse adminView(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found with id: " + id));
        return mapper.fromAddress(address);
    }

    private Address findAddressOrThrow(Long userId, Long addressId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found with id: " + addressId));
    }
}
