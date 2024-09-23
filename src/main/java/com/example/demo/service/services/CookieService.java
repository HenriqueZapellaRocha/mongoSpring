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

    public static void setCookie(final HttpServletResponse response, final String name, final String value) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setPath("/product/getLast");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }


    public static Cookie getCookie(final HttpServletRequest request, final String name) {

        return Arrays.stream(Optional.ofNullable(request.getCookies())
                        .orElseThrow(() -> new CookieNotSetException("No cookie is set")))//this is for case no cookie is set
                        .filter(cookie -> cookie.getName().equals(name))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Cookie not found")); /*this is case the cookie is not found =) */
    }

}
