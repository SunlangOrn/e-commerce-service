package com.liang.address.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String addressLine;
    private String city;
    private String province;
    private String postalCode;
    private Boolean isDefault;
    private Double latitude;
    private Double longitude;
    private String googlePlaceId;
}