package com.xmartin.carregistry.service.impl;

import com.xmartin.carregistry.domain.User;
import com.xmartin.carregistry.entity.UserEntity;
import com.xmartin.carregistry.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder().id(1).name("Xavi").email("test@gmail.com")
                .password("123").role("ROLE_USER").build();
    }

    @Test
    void saveTest() {
        //given
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        //when
        User user = userService.save(userEntity);

        //then
        assertEquals(userEntity.getEmail(), user.getEmail());
    }

    @Test
    void userDetailsServiceTest() {
        //given
        when(userRepository.findByEmailIgnoreCase(userEntity.getEmail())).thenReturn(Optional.ofNullable(userEntity));

        //when
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEntity.getEmail());

        //then
        assertEquals(userEntity.getEmail(), userDetails.getUsername());
        assertEquals(userEntity.getRole(), userDetails.getAuthorities().stream().toList().get(0).getAuthority());
    }
}