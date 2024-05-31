package com.example.ecommerce.config;

import com.example.ecommerce.dto.converter.CustomerDTOConverter;
import com.example.ecommerce.dto.converter.ProductDTOConverter;
import com.example.ecommerce.dto.request.CustomerRequestDTO;
import com.example.ecommerce.dto.request.ProductRequestDTO;
import com.example.ecommerce.model.entity.Customer;
import com.example.ecommerce.model.entity.Product;
import com.example.ecommerce.service.interfaces.CustomerService;
import com.example.ecommerce.service.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Autowired
    private CustomerDTOConverter customerDTOConverter;
    @Autowired
    private ProductDTOConverter productDTOConverter;

    @Bean
    CommandLineRunner initDatabase(CustomerService customerService, ProductService productService) {
        return args -> {
            Customer customer = new Customer();
            customer.setFirstName("Ali");
            customer.setLastName("Aynaci");
            customer.setEmail("aliaynaci@gmail.com");
            customer.setPhoneNumber("1234567890");
            CustomerRequestDTO customerRequestDTO = customerDTOConverter.convertToRequestDto(customer);
            customerService.addCustomer(customerRequestDTO);

            for (int i = 0; i < 5; i++) {
                Product product = new Product();
                product.setName("Product 1");
                product.setPrice(BigDecimal.valueOf(1000));
                ProductRequestDTO productRequestDTO = productDTOConverter.convertToRequestDTO(product);
                productService.createProduct(productRequestDTO);
            }

            for (int i = 0; i < 5; i++) {
                Product product = new Product();
                product.setName("Product 2");
                product.setPrice(BigDecimal.valueOf(2000));
                ProductRequestDTO productRequestDTO = productDTOConverter.convertToRequestDTO(product);
                productService.createProduct(productRequestDTO);
            }

            System.out.println("Veritabanına örnek müşteri ve ürünler eklendi.");
        };
    }
}