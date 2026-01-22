package com.backend.talk_nest.services;

import com.backend.talk_nest.dtos.LoginRequest;
import com.backend.talk_nest.dtos.AuthenticationResponse;
import com.backend.talk_nest.dtos.LogoutRequest;
import com.backend.talk_nest.dtos.RefreshRequest;
import com.backend.talk_nest.entities.InvalidatedToken;
import com.backend.talk_nest.exceptions.AppException;
import com.backend.talk_nest.repositories.InvalidatedTokenRepository;
import com.backend.talk_nest.repositories.UserRepository;
import com.backend.talk_nest.utils.enums.ErrorCode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Value("${jwt.accessTokenExpirationTime}")
    protected Duration ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refreshTokenExpirationTime}")
    protected Duration REFRESH_TOKEN_EXPIRATION_TIME;

    public AuthenticationResponse authenticate(LoginRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.INVALID_AUTHENTICATION);
        }

        var accessToken = generateAccessToken(user.getUsername());
        var refreshToken = generateRefreshToken(user.getUsername());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws JOSEException, ParseException {
        var signedJWT = verifyToken(request.getRefreshToken());

        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return AuthenticationResponse.builder()
                .accessToken(generateAccessToken(user.getUsername()))
                .refreshToken(generateRefreshToken(user.getUsername()))
                .build();
    }

    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        var signedToken = verifyToken(request.getAccessToken());

        String jit = signedToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedToken.getJWTClaimsSet().getExpirationTime();

        long ttl = (expiryTime.getTime() - System.currentTimeMillis()) / 1000;

        if (ttl > 0) {
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .ttl(ttl)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        }
    }


    // Util methods
    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        boolean verified = signedJWT.verify(verifier);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        String jit = signedJWT.getJWTClaimsSet().getJWTID();

        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(jit)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateAccessToken(String username) {
        return generateToken(username, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    private String generateRefreshToken(String username) {
        return generateToken(username, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String generateToken(String username, Duration duration) {

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("talk_nest.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(duration).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Không thể tạo token", e);
        }
    }
}
