package com.example.ecommerce.service;

import com.example.ecommerce.dto.converter.CustomerDTOConverter;
import com.example.ecommerce.dto.request.CustomerRequestDTO;
import com.example.ecommerce.dto.response.CustomerResponseDTO;
import com.example.ecommerce.exception.InvalidEmailException;
import com.example.ecommerce.exception.InvalidPhoneNumberException;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.service.impl.CustomerServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CustomerServiceImplTest {

    private CustomerServiceImpl customerService;
    private CustomerRepository customerRepository;
    private CustomerDTOConverter customerDTOConverter;

    @BeforeEach
    void setUp() {
        customerRepository = Mockito.mock(CustomerRepository.class);
        customerDTOConverter = Mockito.mock(CustomerDTOConverter.class);
        customerService = new CustomerServiceImpl(customerRepository, customerDTOConverter);
    }

    @Test
    void testAddCustomer_whenValidCustomerRequestDTO_shouldReturnCustomerResponseDTO() {
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setFirstName("John");
        customerRequestDTO.setLastName("Doe");
        customerRequestDTO.setEmail("john.doe@gmail.com");
        customerRequestDTO.setPhoneNumber("+1234567890");

        CustomerResponseDTO expectedResponseDTO = new CustomerResponseDTO();
        expectedResponseDTO.setId(1L);
        expectedResponseDTO.setFirstName("John");
        expectedResponseDTO.setLastName("Doe");
        expectedResponseDTO.setEmail("john.doe@gmail.com");
        expectedResponseDTO.setPhoneNumber("+1234567890");

        when(customerService.addCustomer(customerRequestDTO)).thenReturn(expectedResponseDTO);

        CustomerResponseDTO result = customerService.addCustomer(customerRequestDTO);

        assertEquals(expectedResponseDTO, result);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testAddCustomer_whenInvalidEmail_shouldThrowInvalidEmailException() {
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setFirstName("John");
        customerRequestDTO.setLastName("Doe");
        customerRequestDTO.setEmail("john.doe");
        customerRequestDTO.setPhoneNumber("+1234567890");

        assertThrows(InvalidEmailException.class, () -> customerService.addCustomer(customerRequestDTO));
    }

    @Test
    void testAddCustomer_whenInvalidPhoneNumber_shouldThrowInvalidPhoneNumberException() {
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setFirstName("John");
        customerRequestDTO.setLastName("Doe");
        customerRequestDTO.setEmail("john.doe@gmail.com");
        customerRequestDTO.setPhoneNumber("invalid phone number");

        assertThrows(InvalidPhoneNumberException.class, () -> customerService.addCustomer(customerRequestDTO));
    }
}
