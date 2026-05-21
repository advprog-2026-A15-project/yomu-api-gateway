package id.ac.ui.cs.advprog.yomu.gateway.service;

import id.ac.ui.cs.advprog.yomu.shared.grpc.AuthServiceGrpcGrpc;
import id.ac.ui.cs.advprog.yomu.shared.grpc.ValidateTokenRequest;
import id.ac.ui.cs.advprog.yomu.shared.grpc.ValidateTokenResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthClientImpl implements AuthClient {

    @GrpcClient("service-auth")
    private AuthServiceGrpcGrpc.AuthServiceGrpcBlockingStub authServiceStub;

    @Override
    public Mono<ValidateTokenResponse> validateToken(String token) {
        return Mono.fromCallable(() -> {
            ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                    .setToken(token)
                    .build();
            return authServiceStub.validateToken(request);
        });
    }
}
