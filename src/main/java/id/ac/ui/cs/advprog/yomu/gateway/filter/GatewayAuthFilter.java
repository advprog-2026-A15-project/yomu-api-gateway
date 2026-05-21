package id.ac.ui.cs.advprog.yomu.gateway.filter;

import id.ac.ui.cs.advprog.yomu.gateway.service.AuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private final AuthClient authClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicRequest(request)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        
        return authClient.validateToken(token)
                .flatMap(response -> {
                    if (!response.getValid()) {
                        return onError(exchange, HttpStatus.UNAUTHORIZED);
                    }

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", response.getUserId())
                            .header("X-User-Username", response.getUsername())
                            .header("X-User-Role", response.getRole())
                            .build();
                    
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .onErrorResume(e -> {
                    log.error("Error validating token via gRPC", e);
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                });
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicRequest(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (HttpMethod.OPTIONS.equals(method)) {
            return true;
        }

        if (path.equals("/api/auth/register") ||
            path.equals("/api/auth/login") ||
            path.equals("/api/auth/google") ||
            path.equals("/api/auth/refresh")) {
            return true;
        }

        return HttpMethod.GET.equals(method)
            && (isPublicLearningRequest(path)
                || path.startsWith("/api/forum/comments")
                || path.startsWith("/api/clan/leaderboard"));
    }

    private boolean isPublicLearningRequest(String path) {
        return path.equals("/api/learning/bacaan")
            || path.matches("^/api/learning/bacaan/[^/]+$")
            || path.matches("^/api/learning/bacaan/[^/]+/questions$");
    }
}
