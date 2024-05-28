package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.ProductResponseDTO;
import com.example.ecommerce.dto.converter.ProductDTOConverter;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.exception.StockCannotBeNegativeException;
import com.example.ecommerce.model.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.interfaces.ProductService;
import org.springframework.stereotype.Service;

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
    public Product getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return product;
    }

    @Override
    public ProductResponseDTO createProduct(ProductDTO productDTO) {
        // Check if the product already exists in the database
        Optional<Product> existingProduct = productRepository.findByName(productDTO.getName());

        if (existingProduct.isPresent()) {
            // If the product exists, increase the stock by 1
            Product product = existingProduct.get();
            product.setStock(product.getStock() + 1);
            Product updatedProduct = productRepository.save(product);
            return productDTOConverter.convertToDto(updatedProduct);
        } else {
            // If the product does not exist, create a new product
            Product product = productDTOConverter.convertToEntity(productDTO);
            product.setStock(1); // Set the initial stock to 1
            Product createdProduct = productRepository.save(product);
            return productDTOConverter.convertToDto(createdProduct);
        }
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductDTO updatedProductDTO) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProductDTO.getName());
                    product.setPrice(updatedProductDTO.getPrice());
                    Product updatedProduct = productRepository.save(product);
                    return productDTOConverter.convertToDto(updatedProduct);
                })
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProduct(id);
        product.setStock(product.getStock() - 1);

        if (product.getStock() == 0) {
            productRepository.deleteById(id);
        }
        else if (product.getStock() < 0) {
            throw new StockCannotBeNegativeException();
        }
        productRepository.save(product);
    }

}