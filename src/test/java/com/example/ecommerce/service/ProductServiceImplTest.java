package com.example.ecommerce.service;

import com.example.ecommerce.dto.converter.ProductDTOConverter;
import com.example.ecommerce.dto.request.ProductRequestDTO;
import com.example.ecommerce.dto.response.ProductResponseDTO;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.exception.StockNotEnough;
import com.example.ecommerce.model.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDTOConverter productDTOConverter;

    private Product product;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.TEN);
        product.setStock(5);

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Test Product");
        productRequestDTO.setPrice(BigDecimal.TEN);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(1L);
        productResponseDTO.setName("Test Product");
        productResponseDTO.setPrice(BigDecimal.TEN);
    }

    @Test
    void getProduct_productExists_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProduct(1L);

        assertNotNull(result);
        assertEquals(product, result);
    }

    @Test
    void getProduct_productNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(1L));
    }

    @Test
    void createProduct_productNotExists_createsNewProduct() {
        when(productRepository.findByName("Test Product")).thenReturn(Optional.empty());
        when(productDTOConverter.convertToEntity(productRequestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productDTOConverter.convertToDto(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals(1, product.getStock()); // Initial stock is 1
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void createProduct_productExists_increasesStock() {
        when(productRepository.findByName("Test Product")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productDTOConverter.convertToDto(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals(6, product.getStock()); // Stock increased by 1
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_productExists_updatesProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductRequestDTO updatedProductRequestDTO = new ProductRequestDTO();
        updatedProductRequestDTO.setName("Updated Product");
        updatedProductRequestDTO.setPrice(BigDecimal.valueOf(20));
        when(productRepository.save(product)).thenReturn(product);
        when(productDTOConverter.convertToDto(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.updateProduct(1L, updatedProductRequestDTO);

        assertNotNull(result);
        assertEquals("Updated Product", product.getName());
        assertEquals(BigDecimal.valueOf(20), product.getPrice());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProduct_productExistsWithStockGreaterThanOne_decreasesStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertEquals(4, product.getStock()); // Stock decreased by 1
        verify(productRepository, times(1)).save(product);
        verify(productRepository, never()).deleteById(anyLong()); // Shouldn't delete
    }

    @Test
    void deleteProduct_productExistsWithStockOne_deletesProduct() {
        product.setStock(1); // Set stock to 1
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertEquals(0, product.getStock()); // Stock should be 0
        verify(productRepository, times(1)).deleteById(1L); // Should delete
    }

    @Test
    void deleteProduct_productNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void deleteProduct_stockLessThanZero_throwsStockNotEnoughException() {
        product.setStock(-1); // Set negative stock
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(StockNotEnough.class, () -> productService.deleteProduct(1L));
    }

}
