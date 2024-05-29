package com.example.ecommerce.dto.converter;

import com.example.ecommerce.dto.request.CustomerRequestDTO;
import com.example.ecommerce.dto.response.CustomerResponseDTO;
import com.example.ecommerce.model.entity.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerDTOConverter {

    private final ModelMapper modelMapper;

    public CustomerDTOConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Customer convertToEntity(CustomerRequestDTO customerRequestDTO) {
        Customer customer = modelMapper.map(customerRequestDTO, Customer.class);
        return customer;
    }

    public CustomerResponseDTO convertToDto(Customer customer) {
        CustomerResponseDTO customerResponseDTO = modelMapper.map(customer, CustomerResponseDTO.class);
        return customerResponseDTO;
    }
}
