package com.palavecinofranco.products_service.controllers;

import com.palavecinofranco.products_service.model.dtos.ProductRequest;
import com.palavecinofranco.products_service.model.dtos.ProductResponse;
import com.palavecinofranco.products_service.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@EnableMethodSecurity
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addProduct(@RequestBody ProductRequest productRequest){
        this.productService.addProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<ProductResponse> getAllProducts(){
        return this.productService.getAllProducts();
    }
}
