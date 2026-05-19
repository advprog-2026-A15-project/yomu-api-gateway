package id.ac.ui.cs.advprog.yomu.gateway.filter;

import id.ac.ui.cs.advprog.yomu.shared.security.JwtService;
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

@Component
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public GatewayAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isPublicRequest(request)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        try {
            if (!jwtService.isAccessTokenValid(token)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // Extract info to propagate
            String userId = jwtService.extractUserId(token);
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            if (userId != null && username != null && role != null) {
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Username", username)
                        .header("X-User-Role", role)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }

        } catch (Exception e) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        return chain.filter(exchange);
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
