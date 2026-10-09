package org.mohankalia.studenttracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class StudentSecurityConfiguration {

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {

        UserDetails john = User.builder().username("john").password("{noop}test123").roles("STUDENT").build();
        UserDetails mary = User.builder().username("mary").password("{noop}test123").roles("STUDENT", "PROFESSOR").build();
        UserDetails ankesh = User.builder().username("ankesh").password("{noop}test123").roles("STUDENT", "PROFESSOR", "DEAN").build();
        return new InMemoryUserDetailsManager(john);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configure ->
                configure
                        .requestMatchers(HttpMethod.GET, "/api/students").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/students").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.POST, "/api/students").hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasRole("DEAN")
                        .requestMatchers(HttpMethod.PATCH, "/api/students/**").hasRole("PROFESSOR")
                        // adding new request matchers for the university and study level
                        .requestMatchers(HttpMethod.GET, "/api/students/university/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/students/studylevel/**").hasRole("Student")
        );

        // adding basic http authentication
        http.httpBasic(Customizer.withDefaults());

        // diabling csrf not need for this stateless api with basic crud operations

        http.csrf(csrf -> csrf.disable());

        return http.build();

    }


}
