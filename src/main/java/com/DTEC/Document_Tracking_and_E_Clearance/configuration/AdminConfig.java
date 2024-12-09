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
    public ApplicationRunner applicationRunner(){
        return args -> {
            int count = this.userRepository.countSuperAdmin(Role.SUPER_ADMIN);
            if(count == 0){
                User adminZara = new User();
                adminZara.setFirstName("Michael Angelo");
                adminZara.setMiddleName("Buccat");
                adminZara.setLastname("Zara");
                adminZara.setRole(Role.SUPER_ADMIN);
                adminZara.setUsername(username1);
                adminZara.setPassword(this.passwordEncoder.encode(password1));

                User adminTorres = new User();
                adminTorres.setFirstName("Christian James");
                adminTorres.setMiddleName("");
                adminTorres.setLastname("Torres");
                adminTorres.setRole(Role.SUPER_ADMIN);
                adminTorres.setUsername(username2);
                adminTorres.setPassword(this.passwordEncoder.encode(password2));

                this.userRepository.saveAll(Arrays.asList(adminZara, adminTorres));
            }
        };
    }
}