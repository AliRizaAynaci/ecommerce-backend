package com.example.ecommerce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CustomerRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
