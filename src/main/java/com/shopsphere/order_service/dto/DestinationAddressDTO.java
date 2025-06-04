package com.shopsphere.order_service.dto;

import lombok.Data;

@Data
public class DestinationAddressDTO {

    private String number;

    private String street;

    private String city;

    private String state;

    private String postalCode;

    private String country;
}
