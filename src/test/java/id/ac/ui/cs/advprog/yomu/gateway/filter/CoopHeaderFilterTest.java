package id.ac.ui.cs.advprog.yomu.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.Ordered;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoopHeaderFilterTest {

    private CoopHeaderFilter coopHeaderFilter;

    @Mock
    private WebFilterChain filterChain;

    @BeforeEach
    void setUp() {
        coopHeaderFilter = new CoopHeaderFilter();
    }

    @Test
    void testOrderIsHighestPrecedence() {
        assertEquals(Ordered.HIGHEST_PRECEDENCE, coopHeaderFilter.getOrder());
    }

    @Test
    void testFilterAddsCoopHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = coopHeaderFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();

        exchange.getResponse().setComplete().block();
        
        assertEquals("same-origin-allow-popups", 
            exchange.getResponse().getHeaders().getFirst("Cross-Origin-Opener-Policy"));
    }
}
