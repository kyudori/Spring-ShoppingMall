package com.example.shoppingmall.controller.product;

import com.example.shoppingmall.domain.product.Product;
import com.example.shoppingmall.service.product.FileStorageService;
import com.example.shoppingmall.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileStorageService fileStorageService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Product> createProduct(
            @RequestPart("product") Product product,
            @RequestPart("file") MultipartFile file) {

        // 파일 업로드 처리
        String imageUrl = fileStorageService.storeFile(file);
        product.setImageUrl(imageUrl);

        // 상품 생성
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Product> updateProduct(
            @PathVariable("id") Long id,
            @RequestPart("product") Product productDetails,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            Product existingProduct = product.get();
            existingProduct.setName(productDetails.getName());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setStock(productDetails.getStock());

            // 파일이 업로드된 경우 이미지 URL 업데이트
            if (file != null && !file.isEmpty()) {
                String imageUrl = fileStorageService.storeFile(file);
                existingProduct.setImageUrl(imageUrl);
            } else {
                // 파일이 없을 경우, 기존 이미지 URL 유지
                existingProduct.setImageUrl(productDetails.getImageUrl());
            }

            return ResponseEntity.ok(productService.updateProduct(existingProduct));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
