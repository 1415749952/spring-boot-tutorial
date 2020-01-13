package com.mhkj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * securedEnabled：支持 @Secured 注解
 * prePostEnabled：支持 @PreAuthorize 及 @PostAuthorize 注解
 *
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        auth.inMemoryAuthentication().passwordEncoder(passwordEncoder)
                .withUser("user")
                    .password("123")
                    .roles("USER")
                .and()
                .withUser("admin")
                    .password(passwordEncoder.encode("123"))
                    .roles("USER", "ADMIN");
    }

}
