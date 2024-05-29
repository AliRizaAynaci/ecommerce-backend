package com.example.ecommerce.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CustomerResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
