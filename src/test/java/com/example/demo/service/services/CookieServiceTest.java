package com.example.demo.service.services;

import com.example.demo.exception.CookieNotSetException;
import com.example.demo.exception.NotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles( "test" )
public class CookieServiceTest {

    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;


    @Test
    public void CookieService_SetCookie_VoidWithCookieAddedInResponse() {
        String name = "testCookie";
        String value = "testValue";

        CookieService.setCookie(response, name, value);

        verify( response ).addCookie( argThat( cookie ->
                cookie.getName().equals( name ) &&
                        cookie.getValue().equals( value ) &&
                        cookie.getPath().equals( "/product/last" ) &&
                        cookie.isHttpOnly()
        ));
    }

    @Test
    public void testGetCookie_CookieFound() {
        String name = "testCookie";
        Cookie expectedCookie = new Cookie( name, "testValue" );
        Cookie[] cookies = { expectedCookie };

        when( request.getCookies() ).thenReturn( cookies );

        Cookie actualCookie = CookieService.getCookie( request, name );

        assertEquals( expectedCookie, actualCookie );
    }

    @Test
    public void testGetCookie_CookieNotFound() {
        String name = "nonExistentCookie";
        Cookie[] cookies = { new Cookie( "otherCookie", "value" ) };

        when( request.getCookies() ).thenReturn( cookies );

        assertThrows( NotFoundException.class, () -> {
            CookieService.getCookie( request, name );
        });
    }

    @Test
    public void testGetCookie_NoCookiesSet() {
        when( request.getCookies() ).thenReturn( null );

        assertThrows( CookieNotSetException.class, () -> {
            CookieService.getCookie (request, "anyCookie" );
        });
    }
}
