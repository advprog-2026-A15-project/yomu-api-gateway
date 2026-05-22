package id.ac.ui.cs.advprog.yomu.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GatewayConfigTest {

    @Test
    void testConfigLoad() {
        GatewayConfig config = new GatewayConfig();
        assertNotNull(config);
    }
}
