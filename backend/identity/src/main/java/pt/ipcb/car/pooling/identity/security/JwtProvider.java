package pt.ipcb.car.pooling.identity.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JwtProvider {

    @Value("${security.token.secret}")
    private String secretKey;

    @Value("${security.token.issuer}")
    private String tokenIssuer;

    public String validateToken(String token) {
        token = token.replace("Bearer ", "");

        var algorithm = Algorithm.HMAC256(secretKey);

        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    public List<String> getRoles(String token) {
        token = token.replace("Bearer ", "");

        var algorithm = Algorithm.HMAC256(secretKey);

        try {
            DecodedJWT decoded = JWT.require(algorithm)
                    .build()
                    .verify(token);
            List<String> roles = decoded.getClaim("roles").asList(String.class);
            return roles != null ? roles : List.of();
        } catch (JWTVerificationException exception) {
            return Collections.emptyList();
        }
    }
}
