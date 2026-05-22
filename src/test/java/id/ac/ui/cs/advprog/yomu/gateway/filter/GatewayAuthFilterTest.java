package id.ac.ui.cs.advprog.yomu.gateway.filter;

import id.ac.ui.cs.advprog.yomu.shared.grpc.ValidateTokenResponse;
import id.ac.ui.cs.advprog.yomu.gateway.service.AuthClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayAuthFilterTest {

    @Mock
    private AuthClient authClient;

    @Mock
    private GatewayFilterChain filterChain;

    private GatewayAuthFilter gatewayAuthFilter;

    @BeforeEach
    void setUp() {
        gatewayAuthFilter = new GatewayAuthFilter(authClient);
    }

    @Test
    void testOrderIsNegativeOne() {
        assertEquals(-1, gatewayAuthFilter.getOrder());
    }

    @Test
    void testOptionsRequestIsPublic() {
        MockServerHttpRequest request = MockServerHttpRequest.options("/api/secure").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = gatewayAuthFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        verifyNoInteractions(authClient);
    }

    @Test
    void testAuthEndpointsArePublic() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/auth/login").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = gatewayAuthFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        verifyNoInteractions(authClient);
    }

    @Test
    void testPublicGetEndpoints() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/learning/bacaan").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = gatewayAuthFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        verifyNoInteractions(authClient);
    }

    @Test
    void testPublicGetEndpointsId() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/learning/bacaan/123/questions").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = gatewayAuthFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        verifyNoInteractions(authClient);
    }

    @Test
    void testMissingAuthHeaderReturnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/secure").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Void> result = gatewayAuthFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verifyNoInteractions(authClient);
    }

    @Test
    void testInvalidAuthHeaderFormatReturnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/secure")
                .header(HttpHeaders.AUTHORIZATION, "Basic token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Void> result = gatewayAuthFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verifyNoInteractions(authClient);
    }

    @Test
    void testInvalidTokenReturnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/secure")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        ValidateTokenResponse response = ValidateTokenResponse.newBuilder()
                .setValid(false)
                .build();

        when(authClient.validateToken("invalid-token")).thenReturn(Mono.just(response));

        Mono<Void> result = gatewayAuthFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testAuthClientErrorReturnsUnauthorized() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/secure")
                .header(HttpHeaders.AUTHORIZATION, "Bearer error-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(authClient.validateToken("error-token")).thenReturn(Mono.error(new RuntimeException("gRPC error")));

        Mono<Void> result = gatewayAuthFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testValidTokenMutatesRequestAndCallsChain() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/secure")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        ValidateTokenResponse response = ValidateTokenResponse.newBuilder()
                .setValid(true)
                .setUserId("user-123")
                .setUsername("testuser")
                .setRole("USER")
                .build();

        when(authClient.validateToken("valid-token")).thenReturn(Mono.just(response));
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        Mono<Void> result = gatewayAuthFilter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();

        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain).filter(exchangeCaptor.capture());

        ServerWebExchange mutatedExchange = exchangeCaptor.getValue();
        assertEquals("user-123", mutatedExchange.getRequest().getHeaders().getFirst("X-User-Id"));
        assertEquals("testuser", mutatedExchange.getRequest().getHeaders().getFirst("X-User-Username"));
        assertEquals("USER", mutatedExchange.getRequest().getHeaders().getFirst("X-User-Role"));
    }
}
