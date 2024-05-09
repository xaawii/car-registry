package com.xmartin.carregistry.service.impl;

import com.xmartin.carregistry.controller.dtos.JwtResponse;
import com.xmartin.carregistry.controller.dtos.LoginRequest;
import com.xmartin.carregistry.controller.dtos.SignUpRequest;
import com.xmartin.carregistry.domain.User;
import com.xmartin.carregistry.entity.UserEntity;
import com.xmartin.carregistry.exceptions.EmailAlreadyInUseException;
import com.xmartin.carregistry.repository.UserRepository;
import com.xmartin.carregistry.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    /*
    realiza el signup a partir del request
    crea un UserEntity con patron builder, la contraseña la codificamos y el role por defecto
    será user.
     */
    public JwtResponse signup(SignUpRequest request) throws BadRequestException, EmailAlreadyInUseException {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException("Email is already in use");
        }

        var userEntity = UserEntity
                .builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        //guarda el usuario en bbdd
        User user = userService.save(userEntity);
        //genera un token con userdetails de user entity (UserEntity implementa UserDetails)
        var jwt = jwtService.generateToken(user);
        //contruimos la respuesta jwt con el token
        return JwtResponse.builder().token(jwt).build();
    }

    //autentica al usuerio por su email y contraseña del request
    public JwtResponse login(LoginRequest request) {

        //el authenticate por debajo llama al UserServiceImpl (que implementa UserService) para obtener el
        //usuario por el email y compara también la contraseña encriptada, si está tod ok el código sigue
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        //busca el usuario en bbdd, si no lo encuentra lanza una excepción
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        var jwt = jwtService.generateToken(user);
        return JwtResponse.builder().token(jwt).build();

    }
}
