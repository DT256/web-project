package com.group11.service.impl;


import com.group11.entity.*;
import com.group11.repository.*;
import com.group11.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    ProductDetailRepository productDetailRepository;
    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    ImageItemRepository imageItemRepository;

//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private RateRepository rateRepository;

    @Override
    public Page<ProductEntity> searchProducts(@Param("keyword") String keyword, Pageable pageable)
    {
        return productRepository.searchProducts(keyword, pageable);
    }


    @Override
    public Page<ProductEntity> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<ProductEntity> searchProducts(String searchName, String manufacturer, String cpu, String gpu,
                                              String operationSystem, Integer minPrice, Integer maxPrice, String disk, String category, Pageable pageable) {
        // Gọi đến repository để lấy danh sách sản phẩm dựa trên các tiêu chí tìm kiếm
        return productRepository.findProductsByCriteria(searchName, manufacturer, cpu, gpu, operationSystem, minPrice, maxPrice, disk, category, pageable);
    }

//    @Override
//    public List<CategoryModel> getAllCategories() {
//        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
//        return categoryEntities.stream()
//                .map(categoryEntity -> {
//                    CategoryModel categoryModel = new CategoryModel();
//                    categoryModel.setCategoryID(categoryEntity.getCategoryID());
//                    categoryModel.setName(categoryEntity.getName());
//                    categoryModel.setDescription(categoryEntity.getDescription());
//                    return categoryModel;
//                })
//                .collect(Collectors.toList());
//    }


    @Override
    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<ProductEntity> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public ProductEntity save(ProductEntity productEntity) {
        return productRepository.save(productEntity);
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public double calculateAverageRating(ProductEntity product) {
        return 0;
    }

    @Override
    public int getReviewCount(ProductEntity product) {
        return 0;
    }

//    @Override
//    public double calculateAverageRating(ProductEntity product) {
//        Double avgRating = rateRepository.findAverageRatingByProductId(product.getProductID());
//        return avgRating != null ? avgRating : 0.0;
//    }
//
//    @Override
//    public int getReviewCount(ProductEntity product) {
//        Integer reviewCount = rateRepository.findReviewCountByProductId(product.getProductID());
//        return reviewCount != null ? reviewCount : 0;
//    }


	@Override
	public List<ProductEntity> findByName(String producName) {
		// TODO Auto-generated method stub
		return productRepository.findByName(producName);
	}

	@Override
	public Page<ProductEntity> findAllDistinctByName(Pageable pageable) {
		// TODO Auto-generated method stub
		return productRepository.findAllDistinctByName(pageable);
	}

	
	@Override
    public Page<ProductEntity> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    @Override
    public List<ProductEntity> getNewestProducts() {
        List<ProductEntity> allProducts = productRepository.findTop10NewestProducts();
        Collections.reverse(allProducts);
        List<ProductEntity> last10Products = allProducts.subList(0, Math.min(10, allProducts.size()));
        return last10Products;
    }

    @Override
    public ProductEntity saveProduct(ProductEntity product) {
        return productRepository.save(product);
    }
    @Override
    public ProductEntity findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Page<ProductEntity> findByCategory(Long category,  int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryId(category, pageable);
    }

    @Override
    public Page<ProductEntity> findByManufacturer(Long manufacturer,  int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByManufacturerId(manufacturer, pageable);
    }


//
//@Override
//public ProductEntity addProduct(ProductDTO productDTO) {
//    // Chuyển đổi từ DTO sang entity
//    ProductEntity product = new ProductEntity();
//    product.setName(productDTO.getName());
//    product.setPrice(productDTO.getPrice());
//    product.setStatus(ProductStatus.valueOf(productDTO.getStatus()));
//    product.setCategory(categoryRepository.findById(productDTO.getCategoryId())
//            .orElseThrow(() -> new EntityNotFoundException("Category not found")));
//    product.setManufacturer(manufacturerRepository.findById(productDTO.getManufacturerId())
//            .orElseThrow(() -> new EntityNotFoundException("Manufacturer not found")));
//
//    // Lưu chi tiết sản phẩm
//    ProductDetailEntity productDetail = new ProductDetailEntity();
//    productDetail.setRAM(productDTO.getProductDetail().getRAM());
//    productDetail.setCPU(productDTO.getProductDetail().getCPU());
//    productDetail.setGPU(productDTO.getProductDetail().getGPU());
//    productDetail.setMonitor(productDTO.getProductDetail().getMonitor());
//    productDetail.setCharger(productDTO.getProductDetail().getCharger());
//    productDetail.setDisk(productDTO.getProductDetail().getDisk());
//    productDetail.setConnect(productDTO.getProductDetail().getConnect());
//
//    System.out.println("Product detail: "+ productDetail);
//
//    // Lưu hình ảnh
//    List<ImageItemEntity> imageItems = new ArrayList<>();
//    for (ImageItemDTO imageDTO : productDTO.getProductDetail().getImages()) {
//        ImageItemEntity image = new ImageItemEntity();
//        image.setImageUrl(imageDTO.getImageUrl());
//        image.setProductDetail(productDetail);  // Liên kết với productDetail
//        System.out.println("Image ------------"+image);
//        imageItems.add(image);
//    }
//
//    productDetail.setImages(imageItems);
//    productDetailRepository.save(productDetail);
//    // Lưu tất cả các hình ảnh vào DB
//    imageItemRepository.saveAll(imageItems);  // Lưu hình ảnh vào DB
//    // Lưu chi tiết sản phẩm vào DB
//    product.setDetail(productDetail);
//    // Lưu sản phẩm vào DB
//    return productRepository.save(product);
//}



}
