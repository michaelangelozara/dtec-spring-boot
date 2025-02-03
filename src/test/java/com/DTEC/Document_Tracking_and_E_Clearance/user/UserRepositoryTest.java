package com.DTEC.Document_Tracking_and_E_Clearance.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void UserRepository_Save_User_And_Return_User() {

        // Arrange
        User user = User.builder()
                .firstName("First Name")
                .middleName("Middle Name")
                .lastname("Lastname")
                .email("asd@gmail.com")
                .contactNumber("0909090990")
                .username("username")
                .password("password")
                .build();

        // Act
        var savedUser = this.userRepository.save(user);

        // Assert
        Assertions.assertNotNull(savedUser);
        Assertions.assertNotEquals(savedUser.getId(), 0);
    }
}
