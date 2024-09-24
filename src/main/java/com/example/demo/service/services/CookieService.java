package com.example.demo.service.services;

import com.example.demo.exception.NotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import com.example.demo.exception.CookieNotSetException;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CookieService {

    public static void setCookie(final HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/product/last");
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
