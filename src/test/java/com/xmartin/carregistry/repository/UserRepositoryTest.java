package com.xmartin.carregistry.repository;

import com.xmartin.carregistry.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;


    @Test
    void findByEmail_shouldFindUserByEmail() throws Exception {
        //given
        String email = "test@example.com";
        UserEntity user = UserEntity.builder().email(email).name("Xavi").password("123456")
                .role("ROLE_USER").build();

        entityManager.persist(user);

        //when
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        //then
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }

}