package com.group11.restcontroller;

import com.group11.entity.*;
import com.group11.repository.*;
import com.group11.service.IJwtService;
import com.group11.service.IProductService;
import com.group11.service.impl.JwtServiceImpl;
import com.group11.service.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/products")
public class ProductRestController {
    @Autowired
    IJwtService jwtService = new JwtServiceImpl();
    @Autowired
    IProductService productService = new ProductServiceImpl();
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;


    @Autowired
    private ImageItemRepository imageItemRepository;

    @RequestMapping("/newest")
    public ResponseEntity<List<ProductEntity>> getNewestProducts() {
        List<ProductEntity> newestProducts = productService.getNewestProducts();
        return ResponseEntity.ok(newestProducts);
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
