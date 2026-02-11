package com.example.tp_air.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class AuthFilterTest {

    @Test
    void testUnauthorizedAccessRedirectsToLogin() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/TP_AIR/annonce/update");
        when(request.getContextPath()).thenReturn("/TP_AIR");
        when(request.getSession(false)).thenReturn(null);

        AuthFilter filter = new AuthFilter();
        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/TP_AIR/login");
        verify(chain, never()).doFilter(request, response);
    }
}