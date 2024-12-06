package com.group11.restcontroller;


import com.group11.dto.request.PromotionRequest;
import com.group11.entity.PromotionEntity;
import com.group11.repository.PromotionRepository;
import com.group11.service.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
public class PromotionRestController {
    @Autowired
    private IPromotionService promotionService;

    @GetMapping
    public ResponseEntity<Page<PromotionEntity>> getPromotions(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Page<PromotionEntity> promotions = promotionService.getAllPromotions(page, size);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionEntity> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PromotionEntity> getPromotionByCode(@PathVariable String code) {
        return ResponseEntity.ok(promotionService.getPromotionByCode(code));
    }

    @PostMapping
    public ResponseEntity<PromotionEntity> createPromotion(@RequestBody PromotionRequest promotion) {
        return ResponseEntity.ok(promotionService.createPromotion(promotion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionEntity> updatePromotion(@PathVariable Long id, @RequestBody PromotionRequest promotion) {
        return ResponseEntity.ok(promotionService.updatePromotion(promotion, id));
    }


    @DeleteMapping("/{id}")
    public void deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
    }



}