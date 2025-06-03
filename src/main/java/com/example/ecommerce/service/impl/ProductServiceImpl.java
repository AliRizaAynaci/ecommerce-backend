package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.ProductRequestDTO;
import com.example.ecommerce.dto.response.ProductResponseDTO;
import com.example.ecommerce.dto.converter.ProductDTOConverter;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.exception.StockNotEnough;
import com.example.ecommerce.model.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.interfaces.ProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductDTOConverter productDTOConverter;

    public ProductServiceImpl(ProductRepository productRepository, ProductDTOConverter productDTOConverter) {
        this.productRepository = productRepository;
        this.productDTOConverter = productDTOConverter;
    }

    @Override
    @Cacheable(value = "products", key = "#root.methodName", unless = "#result == null")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Cacheable(cacheNames = "product_id", key = "#root.methodName + #id", unless = "#result == null")
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @CacheEvict(value = {"products", "product_id"}, allEntries = true)
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        // Check if the product already exists in the database
        Optional<Product> existingProduct = productRepository.findByName(productRequestDTO.getName());

        if (existingProduct.isPresent()) {
            // If the product exists, increase the stock by 1
            Product product = existingProduct.get();
            product.setStock(product.getStock() + 1);
            Product updatedProduct = productRepository.save(product);
            return productDTOConverter.convertToDto(updatedProduct);
        } else {
            // If the product does not exist, create a new product
            Product product = productDTOConverter.convertToEntity(productRequestDTO);
            product.setStock(1); // Set the initial stock to 1
            Product createdProduct = productRepository.save(product);
            return productDTOConverter.convertToDto(createdProduct);
        }
    }

    @Override
    @CachePut(cacheNames = "product_id", key = "'getProductById' + #id", unless = "#result == null")
    @CacheEvict(value = {"products", "product_id"}, allEntries = true)
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProductRequestDTO) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProductRequestDTO.getName());
                    product.setPrice(updatedProductRequestDTO.getPrice());
                    Product updatedProduct = productRepository.save(product);
                    return productDTOConverter.convertToDto(updatedProduct);
                })
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @CacheEvict(value = {"products", "product_id"}, allEntries = true)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getStock() == null || product.getStock() < 0) {
            throw new StockNotEnough();
        }

        if (product.getStock() > 1) {
            product.setStock(product.getStock() - 1);
            productRepository.save(product);
        } else {
            productRepository.deleteById(id);
        }
    }

}
