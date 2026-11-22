package org.mohankalia.studenttracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class StudentSecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {

        UserDetails john = User.builder()
                .username("john")
                .password(passwordEncoder.encode("test123"))
                .roles("STUDENT")
                .build();
        UserDetails mary = User.builder()
                .username("mary")
                .password(passwordEncoder.encode("test123"))
                .roles("STUDENT", "PROFESSOR")
                .build();
        UserDetails susan = User.builder()
                .username("susan")
                .password(passwordEncoder.encode("test123"))
                .roles("STUDENT", "PROFESSOR", "DEAN")
                .build();
        return new InMemoryUserDetailsManager(john, mary, susan);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configure ->
                configure
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/styles.css", "/app.js", "/students.mjs", "/favicon.svg").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/students").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/students").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.POST, "/api/students").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasRole("DEAN")
                        .requestMatchers(HttpMethod.PATCH, "/api/students/**").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.GET, "/api/students/university/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/students/studylevel/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
        );

        http.httpBasic(Customizer.withDefaults());

        http.csrf(csrf -> csrf.disable());

        return http.build();

    }


}
