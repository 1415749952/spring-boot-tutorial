package com.mhkj;

import com.mhkj.controller.HelloController;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Java6Assertions.assertThat;


@Slf4j
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class SecurityTest {

    @Autowired
    private HelloController helloController;

    private Authentication authentication;

    @Before
    public void init() {
        this.authentication = new UsernamePasswordAuthenticationToken("user","123");
    }

    @After
    public void close() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void secure() {
        assertThatExceptionOfType(AuthenticationException.class)
                .isThrownBy(() -> this.helloController.secure());
    }

    @Test
    public void authenticated() {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        assertThat("Hello Security").isEqualTo(this.helloController.secure());
    }

    @Test
    public void preauth() {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        assertThat("Hello World").isEqualTo(this.helloController.authorized());
    }

    @Test
    public void denied() {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> this.helloController.denied());
    }

}
