package com.mhkj.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhkj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
            .and()
                // 禁用 csrf
                .csrf().disable()
                // 禁用 session
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and()
                .addFilterAfter(jsonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                    .authenticationEntryPoint(new JsonAuthenticationEntryPoint())
                    .accessDeniedHandler(new JsonAccessDeniedHandler());
    }

    @Bean
    public JsonAuthenticationFilter jsonAuthenticationFilter() throws Exception {
        JsonAuthenticationFilter filter = new JsonAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setObjectMapper(this.objectMapper);
        filter.setAuthenticationSuccessHandler(jsonLoginSuccessHandler());
        filter.setAuthenticationFailureHandler(new JsonLoginFailureHandler());
        return filter;
    }

    @Bean
    public JsonLoginSuccessHandler jsonLoginSuccessHandler() {
        return new JsonLoginSuccessHandler(objectMapper);
    }

}
