package id.ac.ui.cs.advprog.yomu.gateway.service;

import id.ac.ui.cs.advprog.yomu.shared.grpc.ValidateTokenResponse;
import reactor.core.publisher.Mono;

public interface AuthClient {
    Mono<ValidateTokenResponse> validateToken(String token);
}