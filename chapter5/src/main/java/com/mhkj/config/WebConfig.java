package com.mhkj.config;

import com.mhkj.filter.TraceFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean webTraceFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new TraceFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

}
