package com.mhkj.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mhkj.entity.Role;
import com.mhkj.entity.Token;
import com.mhkj.entity.User;
import com.mhkj.entity.UserRole;
import com.mhkj.repository.RoleRepository;
import com.mhkj.repository.TokenRepository;
import com.mhkj.repository.UserRepository;
import com.mhkj.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenStr = request.getHeader("token");
        if (tokenStr != null && !tokenStr.isEmpty()) {
            Token tokenDb = tokenRepository.selectOne(new QueryWrapper<Token>().lambda().eq(Token::getToken, tokenStr));
            if (tokenDb != null && tokenDb.getUserId() != null) {
                User user = userRepository.selectById(tokenDb.getUserId());
                if (user == null) {
                    throw new UsernameNotFoundException("token已失效");
                }
                List<UserRole> userRoles = userRoleRepository.selectList(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, user.getId()));
                if (userRoles != null && !userRoles.isEmpty()) {
                    List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
                    List<Role> roles = roleRepository.selectList(new QueryWrapper<Role>().lambda().in(Role::getId, roleIds));
                    user.setRoleList(roles);
                }
                user.setToken(tokenStr);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info(String.format("Authenticated user %s, setting security context", user.getUsername()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

}
