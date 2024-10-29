package com.example.ecommerce.service.interfaces;

import com.example.ecommerce.dto.request.ProductRequestDTO;
import com.example.ecommerce.dto.response.ProductResponseDTO;
import com.example.ecommerce.model.entity.Product;

import java.util.List;

public interface ProductService {

    Product getProductById(Long id);
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProductRequestDTO);
    void deleteProduct(Long id);
    List<Product> getAllProducts();
}
