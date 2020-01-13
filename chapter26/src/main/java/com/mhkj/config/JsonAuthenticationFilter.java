package com.mhkj.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhkj.dto.LoginDto;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Data
public class JsonAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper;

    public JsonAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        // 从输入流中获取到登录的信息
        try {
            LoginDto loginUser = new ObjectMapper().readValue(request.getInputStream(), LoginDto.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getMobile(), loginUser.getPassword())
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
