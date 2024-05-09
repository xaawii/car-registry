package com.xmartin.carregistry.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmartin.carregistry.controller.dtos.JwtResponse;
import com.xmartin.carregistry.controller.dtos.LoginRequest;
import com.xmartin.carregistry.controller.dtos.SignUpRequest;
import com.xmartin.carregistry.controller.mappers.UserMapper;
import com.xmartin.carregistry.exceptions.EmailAlreadyInUseException;
import com.xmartin.carregistry.repository.UserRepository;
import com.xmartin.carregistry.service.impl.AuthenticationServiceImpl;
import com.xmartin.carregistry.service.impl.JwtService;
import com.xmartin.carregistry.service.impl.UserServiceImpl;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationServiceImpl authService;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private UserMapper userMapper;

    /*
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    */


    @Test
        //@WithMockUser(username = "xavi@test.com", roles = "USER")
    void signupTest_valid() throws Exception {

        //given
        SignUpRequest request = SignUpRequest.builder().email("xavi@test.com").name("Xavi").password("123456").build();
        JwtResponse jwtResponse = JwtResponse.builder().token("jjkl").build();

        when(authService.signup(request)).thenReturn(jwtResponse);

        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jjkl"));


    }

    @Test
        //@WithMockUser(username = "xavi@test.com", roles = "USER")
    void signupTest_conflict() throws Exception {

        //given
        SignUpRequest request = SignUpRequest.builder().email("xavi@test.com").name("Xavi").password("123456").build();

        doThrow(new EmailAlreadyInUseException("User already exists"))
                .when(authService).signup(request);


        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

    }

    @Test
        //@WithMockUser(username = "xavi@test.com", roles = "USER")
    void signupTest_badRequest() throws Exception {

        //given
        SignUpRequest request = SignUpRequest.builder().email("xavi@test.com").name("Xavi").password("123456").build();

        doThrow(new BadRequestException()).when(authService).signup(request);


        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
        //@WithMockUser(username = "xavi@test.com", roles = "USER")
    void loginTest_valid() throws Exception {
        //given
        LoginRequest loginRequest = LoginRequest.builder().email("xavi@test.com").password("123456").build();
        JwtResponse jwtResponse = JwtResponse.builder().token("ttt").build();

        when(authService.login(loginRequest)).thenReturn(jwtResponse);


        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("ttt"));
    }

    @Test
        //@WithMockUser(username = "xavi@test.com", roles = "USER")
    void loginTest_unauthorized() throws Exception {
        //given
        LoginRequest loginRequest = LoginRequest.builder().email("xavi@test.com").password("123456").build();

        doThrow(new BadCredentialsException("Incorrect password")).when(authService).login(loginRequest);


        //when - then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isUnauthorized());
    }
}