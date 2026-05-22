package id.ac.ui.cs.advprog.yomu.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testSecurityWebFilterChainBeanExists() {
        SecurityWebFilterChain chain = applicationContext.getBean(SecurityWebFilterChain.class);
        assertNotNull(chain);
    }
}
