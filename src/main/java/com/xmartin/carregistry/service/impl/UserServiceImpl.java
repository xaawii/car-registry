package com.xmartin.carregistry.service.impl;

import com.xmartin.carregistry.domain.User;
import com.xmartin.carregistry.entity.UserEntity;
import com.xmartin.carregistry.exceptions.UserNotFoundException;
import com.xmartin.carregistry.repository.UserRepository;
import com.xmartin.carregistry.service.UserService;
import com.xmartin.carregistry.service.converters.UserConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j

public class UserServiceImpl implements UserService {

    //proporciona una instancia de user repository
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    //guarda un nuevo usuario en la bbdd
    @Override
    public User save(UserEntity newUser) {
        return userConverter.toModel(userRepository.save(newUser));
    }

    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        return userConverter.toModel(userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @Override
    public void deleteUser(String email) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(userEntity);
    }

    @Override
    public void addUserImage(Integer id, MultipartFile file) throws IOException, UserNotFoundException {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        userEntity.setImage(Base64.getEncoder().encode(file.getBytes()));
        userRepository.save(userEntity);

    }

    @Override
    public byte[] getUserImage(Integer id) throws UserNotFoundException {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        return Base64.getDecoder().decode(userEntity.getImage());
    }


    /*
    dentro de este método creamos un objeto UserDetailsService y lo devolvemos, en este objeto debemos definir el
    metodo loadUserByUsername.
    con el username (u otro identificador como el email) devolvemos el usuario como UserDetails
    esto es posible ya que nuestro UserEntity implementa UserDetails
     */
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByEmailIgnoreCase(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }


}
