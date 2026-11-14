package com.app.ecom.dto;

import lombok.Data;

@Data
public class UserRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private AddressDTO address;
}
