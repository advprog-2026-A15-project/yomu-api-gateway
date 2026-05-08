package id.ac.ui.cs.advprog.yomu.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = {
    "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
    "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
    "org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration",
    "org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration",
    "org.springframework.cloud.autoconfigure.RefreshAutoConfiguration",
    "org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration",
    "org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryClientAutoConfiguration",
    "org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration",
    "org.springframework.cloud.client.CommonsClientAutoConfiguration",
    "org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration",
    "org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration"
})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}