package com.example.ecommerce.service.interfaces;

import com.example.ecommerce.dto.request.CustomerRequestDTO;
import com.example.ecommerce.dto.response.CustomerResponseDTO;
import com.example.ecommerce.model.entity.Customer;

public interface CustomerService {

    CustomerResponseDTO addCustomer(CustomerRequestDTO customerRequestDTO);

}
