package org.mohankalia.studenttracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class StudentSecurityConfiguration {

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {

        UserDetails john = User.builder().username("john").password("{noop}test123").roles("STUDENT").build();
        UserDetails mary = User.builder().username("mary").password("{noop}test123").roles("PROFESSOR").build();
        UserDetails ankesh = User.builder().username("ankesh").password("{noop}test123").roles("DEAN").build();

        return new InMemoryUserDetailsManager(john);
    }
}
