package id.ac.ui.cs.advprog.yomu.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CorsConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testCorsWebFilterBeanExists() {
        CorsWebFilter filter = applicationContext.getBean(CorsWebFilter.class);
        assertNotNull(filter);
    }
}
