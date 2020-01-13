package com.mhkj.utils;

import com.mhkj.entity.Role;
import com.mhkj.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JwtUtils {

    private static final String SUBJECT = "testJwt";
    private static final String SECRET_KEY = "2356874236547";
    private static final long EXPIRE = 1000 * 24 * 60 * 60 * 7;

    public static String genJwtToken(User user) {
        String roles = Optional.ofNullable(user.getRoleList())
                .orElse(new ArrayList<>()).stream()
                .map(Role::getRolename)
                .collect(Collectors.joining(","));
        String token = Jwts
                .builder()
                .setSubject(SUBJECT)
                .claim("id", user.getId())
                .claim("name", user.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
        return token;
    }

    public static User getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        String name = claims.get("name").toString();
        if (name != null && !name.isEmpty()) {
            User user = new User();
            user.setUsername(name);
            user.setId(Long.parseLong(claims.get("id").toString()));
            String roles = claims.get("roles").toString();
            if (roles != null && !roles.isEmpty()) {
                List<Role> roleList = Stream.of(roles.split(",")).filter(v -> !StringUtils.isEmpty(v)).map(v -> {
                    Role role = new Role();
                    role.setRolename(v);
                    return role;
                }).collect(Collectors.toList());
                user.setRoleList(roleList);
            }
            return user;
        }
        return null;
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(1L);
        user.setUsername("gmm");
        user.setRoleList(new ArrayList<>());
        user.getRoleList().add(new Role(1L, "ROLE_ADMIN"));
        user.getRoleList().add(new Role(2L, "ROLE_USER"));
        String token = genJwtToken(user);
        System.out.println(token);
        User authentication = getAuthentication(token);
        if (authentication != null) {
            System.out.println(authentication);
        }
    }

}
