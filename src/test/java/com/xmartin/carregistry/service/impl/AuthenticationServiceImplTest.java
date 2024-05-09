package com.xmartin.carregistry.service.impl;

import com.xmartin.carregistry.controller.dtos.JwtResponse;
import com.xmartin.carregistry.controller.dtos.LoginRequest;
import com.xmartin.carregistry.controller.dtos.SignUpRequest;
import com.xmartin.carregistry.domain.User;
import com.xmartin.carregistry.entity.UserEntity;
import com.xmartin.carregistry.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @InjectMocks
    private AuthenticationServiceImpl authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder().id(1).name("Xavi").email("test@gmail.com")
                .password("123").role("ROLE_USER").build();
    }

    @SneakyThrows
    @Test
    void signupTest() {
        //given
        SignUpRequest signUpRequest = SignUpRequest.builder().email("test@gmail.com")
                .name("Xavi").password("123456").build();

        User user = user = User.builder().id(1).email("test@gmail.com")
                .name("Xavi").password("123456").role("ROLE_USER").build();

        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("456");
        when(userService.save(any(UserEntity.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jjkl5");

        //when
        JwtResponse jwtResponse = authService.signup(signUpRequest);

        //then
        assertEquals("jjkl5", jwtResponse.getToken());
    }

    @Test
    void loginTest() {
        LoginRequest loginRequest = LoginRequest.builder().email("test@gmail.com")
                .password("123456").build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(userEntity));
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("uwtx");

        //when
        JwtResponse jwtResponse = authService.login(loginRequest);

        //then
        assertEquals("uwtx", jwtResponse.getToken());
    }
}