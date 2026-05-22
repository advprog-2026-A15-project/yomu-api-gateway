package id.ac.ui.cs.advprog.yomu.gateway.service;

import id.ac.ui.cs.advprog.yomu.shared.grpc.AuthServiceGrpcGrpc;
import id.ac.ui.cs.advprog.yomu.shared.grpc.ValidateTokenRequest;
import id.ac.ui.cs.advprog.yomu.shared.grpc.ValidateTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthClientImplTest {

    @InjectMocks
    private AuthClientImpl authClient;

    @Mock
    private AuthServiceGrpcGrpc.AuthServiceGrpcBlockingStub authServiceStub;

    @BeforeEach
    void setUp() throws Exception {
        // Use reflection to inject the private grpc stub since @GrpcClient doesn't have a setter
        Field stubField = AuthClientImpl.class.getDeclaredField("authServiceStub");
        stubField.setAccessible(true);
        stubField.set(authClient, authServiceStub);
    }

    @Test
    void testValidateTokenSuccess() {
        ValidateTokenResponse grpcResponse = ValidateTokenResponse.newBuilder()
                .setValid(true)
                .setUserId("user123")
                .setUsername("testuser")
                .setRole("USER")
                .build();

        when(authServiceStub.validateToken(any(ValidateTokenRequest.class))).thenReturn(grpcResponse);

        Mono<ValidateTokenResponse> result = authClient.validateToken("some-token");

        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getValid() && 
                    "user123".equals(response.getUserId()) &&
                    "testuser".equals(response.getUsername())
                )
                .verifyComplete();
    }

    @Test
    void testValidateTokenError() {
        when(authServiceStub.validateToken(any(ValidateTokenRequest.class)))
            .thenThrow(new RuntimeException("gRPC call failed"));

        Mono<ValidateTokenResponse> result = authClient.validateToken("bad-token");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && 
                                               "gRPC call failed".equals(throwable.getMessage()))
                .verify();
    }
}
