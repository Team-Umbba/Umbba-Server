package sopt.org.umbbaServer.global.config.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import sopt.org.umbbaServer.global.config.jwt.redis.RefreshToken;
import sopt.org.umbbaServer.global.config.jwt.redis.TokenRepository;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {


    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 1000L * 60;  // 액세스 토큰 만료 시간: 1시간으로 지정
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 60 * 1000L * 180;  // 리프레시 토큰 만료 시간: 3시간으로 지정

    @Value("${jwt.secret}")
    private String JWT_SECRET;
    private final TokenRepository tokenRepository;

    @PostConstruct
    protected void init() {
        JWT_SECRET = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }


    public TokenDto issueToken(Authentication authentication) {
        return TokenDto.of(
                generateAccessToken(authentication),
                generateRefreshToken(authentication));
    }

    // Access 토큰 생성
    private String generateAccessToken(Authentication authentication) {
        final Date now = new Date();

        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME));

        claims.put("userId", authentication.getPrincipal());

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    // Refresh 토큰 생성
    /**
     * Redis 내부에는
     * refreshToken:userId : tokenValue 형태로 저장한다.
     * accessToken과 다르게 UUID로 생성한다.
     */
    private String generateRefreshToken(Authentication authentication) {
        final Date now = new Date();

        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME));

        // TODO UUID or JWT 토큰으로 암호화하여 생성
        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();

        tokenRepository.save(
                RefreshToken.builder()
                        .id(Long.parseLong(authentication.getPrincipal().toString()))
                        .refreshToken(refreshToken)
                        .expiration(REFRESH_TOKEN_EXPIRATION_TIME.intValue() / 1000)
                        .build()
        );

        return refreshToken;
    }

    // Access 토큰 검증
    public JwtValidationType validateAccessToken(String accessToken) {
        try {
            final Claims claims = getBody(accessToken);
            log.info(JwtValidationType.VALID_JWT.getValue());
            return JwtValidationType.VALID_JWT;
        } catch (MalformedJwtException ex) {
            log.error(JwtValidationType.INVALID_JWT_TOKEN.getValue());
            return JwtValidationType.INVALID_JWT_TOKEN;
        } catch (ExpiredJwtException ex) {
            log.error(JwtValidationType.EXPIRED_JWT_TOKEN.getValue());
            return JwtValidationType.EXPIRED_JWT_TOKEN;
        } catch (UnsupportedJwtException ex) {
            log.error(JwtValidationType.UNSUPPORTED_JWT_TOKEN.getValue());
            return JwtValidationType.UNSUPPORTED_JWT_TOKEN;
        } catch (IllegalArgumentException ex) {
            log.error(JwtValidationType.EMPTY_JWT.getValue());
            return JwtValidationType.EMPTY_JWT;
        }
    }

    // Refresh 토큰 검증
    public boolean validateRefreshToken(Long userId, String refreshToken) throws Exception {
        // 해당유저의 Refresh 토큰 만료 : Redis에 해당 유저의 토큰이 존재하지 않음
        RefreshToken token = tokenRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorType.INVALID_REFRESH_TOKEN));

        if (token.getRefreshToken() == null) {
            return false;
        }
        else return token.getRefreshToken().equals(refreshToken);
    }

    public void deleteRefreshToken(Long userId) {
        tokenRepository.deleteById(userId);
    }

    // 토큰에 담겨있는 userId 획득
    public Long getUserFromJwt(String token) {
        Claims claims = getBody(token);
        return Long.parseLong(claims.get("userId").toString());
    }

    private Claims getBody(final String token) {
        // 만료된 토큰에 대해 parseClaimsJws를 수행하면 io.jsonwebtoken.ExpiredJwtException이 발생한다.
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes());
        return Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    public static Long getUserFromPrincial(Principal principal) {
        if (isNull(principal)) {
            throw new CustomException(ErrorType.EMPTY_PRINCIPLE_EXCEPTION);
        }
        return Long.valueOf(principal.getName());
    }
}
