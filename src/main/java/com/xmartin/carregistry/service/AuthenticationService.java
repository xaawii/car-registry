package com.xmartin.carregistry.service;

import com.xmartin.carregistry.controller.dtos.JwtResponse;
import com.xmartin.carregistry.controller.dtos.LoginRequest;
import com.xmartin.carregistry.controller.dtos.SignUpRequest;
import com.xmartin.carregistry.exceptions.EmailAlreadyInUseException;
import org.apache.coyote.BadRequestException;

public interface AuthenticationService {
    public JwtResponse signup(SignUpRequest request) throws BadRequestException, EmailAlreadyInUseException;
    public JwtResponse login(LoginRequest request);
}
