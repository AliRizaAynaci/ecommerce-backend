package com.example.ecommerce.service.interfaces;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.model.entity.Product;

public interface ProductService {

    Product getProduct(Long id);
    ProductResponseDTO createProduct(ProductDTO productDTO);
    ProductResponseDTO updateProduct(Long id, ProductDTO updatedProductDTO);
    void deleteProduct(Long id);
}
