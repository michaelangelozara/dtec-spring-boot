package com.DTEC.Document_Tracking_and_E_Clearance.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final LogoutHandler logoutHandler;

    @Value("${frontend.origin}")
    private String FRONT_END_DOMAIN;

    @Value("${header}")
    private String HEADER;

    private final String[] ALL_ROLES = {
            "SUPER_ADMIN",
            "ADMIN",
            "OFFICE_IN_CHARGE",
            "PERSONNEL",
            "MODERATOR",
            "STUDENT",
            "STUDENT_OFFICER",
            "DSA",
            "PRESIDENT",
            "COMMUNITY",
            "FINANCE",
            "OFFICE_HEAD",
            "GUIDANCE",
            "DEAN",
            "CASHIER",
            "LIBRARIAN",
            "SCHOOL_NURSE",
            "PROGRAM_HEAD",
            "REGISTRAR",
            "SCIENCE_LAB",
            "COMPUTER_SCIENCE_LAB",
            "ELECTRONICS_LAB",
            "CRIM_LAB",
            "HRM_LAB",
            "NURSING_LAB",
            "ACCOUNTING_CLERK",
            "CUSTODIAN",
            "VPAF",
            "VPA",
            "MULTIMEDIA"
    };
    private final String[] STAFF_ROLES = {
            "SUPER_ADMIN",
            "ADMIN",
            "OFFICE_IN_CHARGE",
            "MODERATOR",
            "STUDENT_OFFICER",
            "DSA",
            "PRESIDENT",
            "COMMUNITY",
            "FINANCE",
            "OFFICE_HEAD"
    };
    private final String[] ADMIN = {"SUPER_ADMIN", "ADMIN"};

    private final String[] CLEARANCE_ROLES = {
            "SUPER_ADMIN",
            "ADMIN",
            "STUDENT",
            "PERSONNEL",
            "STUDENT_OFFICER",
            "GUIDANCE",
            "DEAN",
            "PRESIDENT",
            "FINANCE",
            "CASHIER",
            "LIBRARIAN",
            "SCHOOL_NURSE",
            "PROGRAM_HEAD",
            "REGISTRAR",
            "DSA",
            "SCIENCE_LAB",
            "COMPUTER_SCIENCE_LAB",
            "ELECTRONICS_LAB",
            "CRIM_LAB",
            "HRM_LAB",
            "NURSING_LAB",
            "ACCOUNTING_CLERK",
            "CUSTODIAN",
            "VPAF",
            "VPA",
            "MULTIMEDIA"
    };

    public SecurityConfig(
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            LogoutHandler logoutHandler
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/v1/users/**").hasAnyRole(ALL_ROLES)
                        .requestMatchers("/api/v1/implementation-letter-in-campuses/**").hasAnyRole(STAFF_ROLES)
                        .requestMatchers("/api/v1/implementation-letter-off-campuses/**").hasAnyRole(STAFF_ROLES)
                        .requestMatchers("/api/v1/communication-letters/**").hasAnyRole(STAFF_ROLES)
                        .requestMatchers("/api/v1/budget-proposals/**").hasAnyRole(STAFF_ROLES)
                        .requestMatchers("/api/v1/generic-letters/**").hasAnyRole(STAFF_ROLES)
                        .requestMatchers("/api/v1/clearances/**").hasAnyRole(CLEARANCE_ROLES)
                        .requestMatchers("/api/v1/admin/**").hasAnyRole(ADMIN)
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> {
                    exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );

        return http.build();

    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(
                    Arrays.asList(FRONT_END_DOMAIN)
            );
            configuration.setAllowedMethods(
                    Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
            );
            configuration.setAllowedHeaders(Arrays.asList(HEADER, "Content-Type"));
            configuration.setExposedHeaders(Arrays.asList(HEADER));
            configuration.setAllowCredentials(true);
            configuration.setMaxAge(3600L);
            return configuration;
        };
    }
}
