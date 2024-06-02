package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.converter.CustomerDTOConverter;
import com.example.ecommerce.dto.request.CustomerRequestDTO;
import com.example.ecommerce.dto.response.CustomerResponseDTO;
import com.example.ecommerce.exception.InvalidEmailException;
import com.example.ecommerce.exception.InvalidPhoneNumberException;
import com.example.ecommerce.model.entity.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.service.interfaces.CustomerService;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService {


    private static final Pattern EMAIL_REGEX = Pattern.compile
            ("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\+?[0-9\\s\\-()]{10,15}$");

    private final CustomerRepository customerRepository;
    private final CustomerDTOConverter customerDTOConverter;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               CustomerDTOConverter customerDTOConverter) {
        this.customerRepository = customerRepository;
        this.customerDTOConverter = customerDTOConverter;
    }

    @Override
    public CustomerResponseDTO addCustomer(CustomerRequestDTO customerRequestDTO) {
        validateEmail(customerRequestDTO.getEmail());
        validatePhoneNumber(customerRequestDTO.getPhoneNumber());

        Customer customer = customerDTOConverter.convertToEntity(customerRequestDTO);
        Customer createdCustomer = customerRepository.save(customer);
        return customerDTOConverter.convertToDto(createdCustomer);
    }

    private void validateEmail(String email) {
        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (!PHONE_REGEX.matcher(phoneNumber).matches()) {
            throw new InvalidPhoneNumberException("Invalid phone number format: " + phoneNumber);
        }
    }
}
