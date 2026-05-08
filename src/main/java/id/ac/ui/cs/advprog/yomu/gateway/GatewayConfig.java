package id.ac.ui.cs.advprog.yomu.gateway;

import id.ac.ui.cs.advprog.yomu.shared.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }
}
