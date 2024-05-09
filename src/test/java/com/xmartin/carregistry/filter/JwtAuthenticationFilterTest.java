package com.xmartin.carregistry.filter;

import com.xmartin.carregistry.entity.UserEntity;
import com.xmartin.carregistry.service.impl.JwtService;
import com.xmartin.carregistry.service.impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserServiceImpl userService;

    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;
    @Mock
    private FilterChain filterChain;
    @Mock
    private UserDetailsService userDetailsService;

    @SneakyThrows
    @Test
    void doFilterInternal() {

        //given
        UserDetails userDetails = UserEntity.builder().id(1).email("test@gmail.com").name("Xavi")
                .password("123").role("ROLE_USER").build();


        when(request.getHeader("Authorization")).thenReturn("Bearer klp2");
        when(jwtService.extractUserName("klp2")).thenReturn("test@gmail.com");
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userService.userDetailsService().loadUserByUsername("test@gmail.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("klp2", userDetails)).thenReturn(true);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        verify(filterChain).doFilter(request, response);
    }
}