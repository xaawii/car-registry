package com.xmartin.carregistry.service;

import com.xmartin.carregistry.domain.User;
import com.xmartin.carregistry.entity.UserEntity;
import com.xmartin.carregistry.exceptions.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    public User save(UserEntity newUser);

    public User getUserByEmail(String email) throws UserNotFoundException;

    public void deleteUser(String email) throws UserNotFoundException;

    public void addUserImage(Integer id, MultipartFile file) throws IOException, UserNotFoundException;

    public byte[] getUserImage(Integer id) throws UserNotFoundException;
}
