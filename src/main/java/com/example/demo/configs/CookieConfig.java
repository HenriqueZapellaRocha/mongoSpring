//package com.example.demo.configs;
//
//
//import com.example.demo.service.services.CookieService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@RequiredArgsConstructor
//public class CookieConfig implements WebMvcConfigurer {
//
//    private final CookieService cookieInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // Apply the interceptor only to the specified paths
//        registry.addInterceptor(cookieInterceptor)
//                .addPathPatterns("/product/last", "/product/{id}");
//    }
//}

