package com.group11.restcontroller;

import com.group11.dto.response.ProductResponse;
import com.group11.entity.*;
import com.group11.repository.*;
import com.group11.service.IInventoryService;
import com.group11.service.IJwtService;
import com.group11.service.IProductService;
import com.group11.service.impl.JwtServiceImpl;
import com.group11.service.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {
    @Autowired
    IJwtService jwtService = new JwtServiceImpl();
    @Autowired
    IProductService productService = new ProductServiceImpl();
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IInventoryService inventoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;


    @Autowired
    private ImageItemRepository imageItemRepository;

    @PutMapping("/updatePrice/{id}")
    public ResponseEntity<?> updateProductPrice(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            int newPrice = Integer.parseInt(payload.get("price").toString());
            ProductEntity productEntity = productService.findById(id);
            productEntity.setPrice(newPrice);
            productRepository.save(productEntity);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating price");
        }
    }

    @RequestMapping("/newest")
    public ResponseEntity<List<ProductEntity>> getNewestProducts() {
        List<ProductEntity> newestProducts = productService.getNewestProducts();
        return ResponseEntity.ok(newestProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductEntity productEntity = productService.findById(id);
        int quantity = inventoryService.findById(id).get().getQuantity();
        return ResponseEntity.ok(new ProductResponse(productEntity, quantity));
    }

    @GetMapping("/getAll")
    public ResponseEntity<Page<ProductEntity>> getAllProducts(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Page<ProductEntity>> getAllProductsByCategoies(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Page<ProductEntity> products = productService.findByCategory(id, page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/manufacturer/{id}")
    public ResponseEntity<Page<ProductEntity>> getAllProductsByManufacturer(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Page<ProductEntity> products = productService.findByManufacturer(id, page, size);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeProduct(@RequestHeader("Authorization") String authorizationHeader,@PathVariable Long productId) {
        try {
            String token = authorizationHeader.substring(7); // Bỏ chữ "Bearer "

            // Trích xuất userId từ token
            Long userId = jwtService.extractUserId(token);
            // Tìm sản phẩm theo id
            ProductEntity product = productService.findById(productId);
            if(product.getStatus() == ProductStatus.AVAILABLE)
            {
                product.setStatus(ProductStatus.UNAVAILABLE);
                productRepository.save(product);
                return ResponseEntity.ok("Product removed successfully");
            }
            else {
                return ResponseEntity.status(404).body("Product not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while removing the product");
        }
    }








}
