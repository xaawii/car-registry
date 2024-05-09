package com.xmartin.carregistry.filter;

import com.xmartin.carregistry.service.impl.JwtService;
import com.xmartin.carregistry.service.impl.UserServiceImpl;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceImpl userService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        /*
        si el header viene vacío, continuaremos los filtros sin validar (por eso ejecutamos igualmente
        filterChain.doFilter) y saldremos del código.
        Usamos StringUtils porque usar directamente .isEmpty da error null pointer
         */
        if (StringUtils.isEmpty(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        //a partir de la posición 7 obtenemos el token porque empieza por Bearer XXX
        jwt = authHeader.substring(7);
        log.info("JWT -> {}", jwt);

        userEmail = jwtService.extractUserName(jwt);

        if (!StringUtils.isEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);

            //log.info("user details en jwtauth: " + userDetails);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("User -> {}", userDetails);
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                //obtenemos un token de autentificacion a partir de los datos del usuario y sus roles
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                //le añadimos los detalles de la request
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //añadimos la autentificacion al contexto
                context.setAuthentication(authToken);
                //añadimos el contexto a la petición que estabamos haciendo
                SecurityContextHolder.setContext(context);
            }
        }
        //En este punto el filterChain se ejecuta después de haber validado el token
        filterChain.doFilter(request, response);
    }
}
