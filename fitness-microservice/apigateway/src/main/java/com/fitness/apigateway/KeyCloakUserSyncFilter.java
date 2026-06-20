package com.fitness.apigateway;


import com.fitness.apigateway.user.RegisterRequest;
import com.fitness.apigateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");

        if (token == null || !token.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        RegisterRequest registerRequest = getUserDetail(token);

        log.info("UserId from token: {}", userId);
        log.info("RegisterRequest: {}", registerRequest);

        // ✅ Null safety (IMPORTANT)
        if (registerRequest == null) {
            log.error("Failed to parse token");
            return chain.filter(exchange);
        }

        if (userId == null) {
            userId = registerRequest.getKeyCloakId();
        }

        if (userId != null) {
            String finalUserId = userId;

            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        log.info("User exists? {}", exist);

                        if (!exist) {
                            return userService.registerUser(registerRequest)
                                    .doOnSuccess(res -> log.info("User registered"))
                                    .then(userService.validateUser(finalUserId)); // ✅ re-check
                        }

                        return Mono.just(true);
                    })
                    .then(Mono.defer(() -> {
                        ServerHttpRequest mutatedRequest = exchange.getRequest()
                                .mutate()
                                .header("X-User-ID", finalUserId)
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }

        return chain.filter(exchange);
    }


    private RegisterRequest getUserDetail(String token) {
        try{
            String tokenWithoutBearer=token.replace("Bearer ","").trim();
            SignedJWT signedJWT=SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet=signedJWT.getJWTClaimsSet();
            RegisterRequest registerRequest=new RegisterRequest();
            registerRequest.setEmail(claimsSet.getStringClaim("email"));
            registerRequest.setKeyCloakId(claimsSet.getStringClaim("sub"));
            registerRequest.setPassword("dummy@123");
            registerRequest.setFirstName(claimsSet.getStringClaim("given_name"));
            registerRequest.setLastName(claimsSet.getStringClaim("family_name"));

            return registerRequest;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
