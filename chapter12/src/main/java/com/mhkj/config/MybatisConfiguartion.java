package com.mhkj.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfiguartion {

    /**
     * 配置分页插件
     */
    @Bean
    @ConditionalOnClass(BaseMapper.class)
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
