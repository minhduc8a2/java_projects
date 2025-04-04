package com.example.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.exception.EntityNotFoundException;
import com.example.mappers.ProductMapper;
import com.example.model.dto.ProductDTO;
import com.example.model.entity.Product;
import com.example.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO addProduct(ProductDTO productDTO) {
        Product product = productMapper.productDTOToProduct(productDTO);
        Product addedProduct = productRepository.save(product);
        return productMapper.productToProductDTO(addedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO updateProduct(long productId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());

        Product updatedProduct = productRepository.save(product);

        return productMapper.productToProductDTO(updatedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(long productId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.productsToProductDTOs(products);
    }

    public ProductDTO getProductById(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return productMapper.productToProductDTO(product);
    }

}
