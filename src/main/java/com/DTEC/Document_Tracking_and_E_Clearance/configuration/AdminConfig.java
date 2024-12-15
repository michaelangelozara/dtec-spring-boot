package com.DTEC.Document_Tracking_and_E_Clearance.configuration;


import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AdminConfig {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admins.admin1.username}")
    private String username1;
    @Value("${admins.admin1.password}")
    private String password1;

    @Value("${admins.admin2.username}")
    private String username2;
    @Value("${admins.admin2.password}")
    private String password2;

    public AdminConfig(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean(name = "admin-initializer")
    public ApplicationRunner applicationRunner() {
        return args -> {
            int count = this.userRepository.countSuperAdmin(Role.SUPER_ADMIN);
            if (count == 0) {
                User adminZara = User.builder()
                        .firstName("Michael Angelo")
                        .middleName("Buccat")
                        .lastname("Zara")
                        .email("michaelangelobuccatzara@gmail.com")
                        .role(Role.SUPER_ADMIN)
                        .username(username1)
                        .password(this.passwordEncoder.encode(password1))
                        .build();

                User adminTorres = User.builder()
                        .firstName("Christian James")
                        .middleName("")
                        .lastname("Torres")
                        .email("charistialjamestorres@gmail.com")
                        .role(Role.MODERATOR)
                        .username(username2)
                        .password(this.passwordEncoder.encode(password2))
                        .build();

                this.userRepository.saveAll(Arrays.asList(adminZara, adminTorres));
            }
        };
    }
}