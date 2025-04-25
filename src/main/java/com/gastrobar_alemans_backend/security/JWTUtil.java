package com.gastrobar_alemans_backend.security;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    private static final String secureToken = "tokenlogin";
    private static final long ACCESS_TOKEN_DURATION = 5 * 60 * 1000;
    private static final long REFRESH_TOKEN_DURATION = 7 * 24 * 60 * 60 * 1000;

    public String generateToken(String correo) {
        return JWT.create()
                .withSubject(correo)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_DURATION))
                .sign(Algorithm.HMAC256(secureToken));
    }

    public String generateRefreshToken(String correo) {
        return JWT.create()
                .withSubject(correo)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_DURATION))
                .sign(Algorithm.HMAC256(secureToken));
    }

    public String validateTokenAndRetrieveSubject(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secureToken))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String correo = validateTokenAndRetrieveSubject(token);
        return correo != null && correo.equals(userDetails.getUsername());
    }

}
