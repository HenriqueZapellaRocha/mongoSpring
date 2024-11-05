package com.example.demo.service.services;

import com.example.demo.exception.NotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.demo.exception.CookieNotSetException;

import java.util.Arrays;
import java.util.Optional;

@Service
@Component
public class CookieService {

    public static void setCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath( "/product/last" );
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }


    public static Cookie getCookie(HttpServletRequest request, String name) {
        return Arrays.stream(Optional.ofNullable(request.getCookies())
                        //this is for case no cookie is set
                        .orElseThrow(() -> new CookieNotSetException("No cookie is set")))
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                //this is case the cookie is not found =)
                .orElseThrow(() -> new NotFoundException("Cookie not found"));
    }

}

//package com.example.demo.service.services;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//@RequiredArgsConstructor
//public class CookieService implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//
//        String requestURI = request.getRequestURI();
//        String productId = requestURI.substring(requestURI.lastIndexOf("/") + 1);
//
//
//        Cookie lastCookie = findCookie(request, "last");
//
//        if (lastCookie == null) {
//            lastCookie = new Cookie("last", productId);
//            lastCookie.setPath("/product/last");
//            lastCookie.setHttpOnly(true);
//            response.addCookie(lastCookie);
//        }
//
//        // Store cookie value in the request for controller access
//        request.setAttribute("lastCookieValue", lastCookie.getValue());
//        return true; // Allow the request to proceed
//    }
//
//    private Cookie findCookie(HttpServletRequest request, String name) {
//        if (request.getCookies() == null) {
//            return null;
//        }
//        for (Cookie cookie : request.getCookies()) {
//            if (name.equals(cookie.getName())) {
//                return cookie;
//            }
//        }
//        return null;
//    }
//}

