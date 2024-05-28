package com.example.ecommerce.dto.converter;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.model.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOConverter {

    private final ModelMapper modelMapper;

    public ProductDTOConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Product convertToEntity(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        return product;
    }

    public ProductResponseDTO convertToDto(Product product) {
        ProductResponseDTO productResponseDTO = modelMapper.map(product, ProductResponseDTO.class);
        return productResponseDTO;
    }
}