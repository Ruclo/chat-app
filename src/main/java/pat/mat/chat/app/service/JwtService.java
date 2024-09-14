package pat.mat.chat.app.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import pat.mat.chat.app.model.PersistentToken;
import pat.mat.chat.app.repository.PersistentTokenRepository;
import pat.mat.chat.app.exception.PotentialCookieTheftException;
import pat.mat.chat.app.util.TokenPair;
import pat.mat.chat.app.model.User;
import pat.mat.chat.app.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class JwtService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PersistentTokenRepository persistentTokenRepository;

    public static final int REFRESH_TOKEN_DURATION_IN_DAYS = 30;

    public static final int ACCESS_TOKEN_DURATION_IN_MINUTES = 15;

    public static final int SUCCESSIONID_LENGTH = 16;

    public TokenPair generateTokenPair(User user, String successionId) {

        Jwt refreshToken = generateRefreshToken(user.getUsername(), successionId);
        Jwt accessToken = generateAccessToken(user.getUsername());

        String refreshTokenString = refreshToken.getTokenValue();
        String accessTokenString = accessToken.getTokenValue();

        PersistentToken persistentToken = new PersistentToken(successionId,
                hashToken(refreshTokenString),
                user,
                refreshToken.getExpiresAt());
        persistentTokenRepository.save(persistentToken);

        return new TokenPair(accessTokenString, refreshTokenString);
    }

    public TokenPair generateTokenPair(User user) {
        return generateTokenPair(user, generateSuccessionId());
    }

    public User getUserFromToken(String jwtString) {
        Jwt jwt = jwtDecoder.decode(jwtString);
        return userRepository.findByUsername(jwt.getSubject()).orElseThrow();
    }

    public TokenPair rotateAndRefreshTokens(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);
        String successionId = jwt.getClaimAsString("successionId");

        PersistentToken persistentToken = persistentTokenRepository.findById(successionId).orElseThrow(PotentialCookieTheftException::new);

        if (!persistentToken.getTokenHash().equals(hashToken(refreshToken))) {
            throw new PotentialCookieTheftException();
        }

        User user = getUserFromToken(refreshToken);
        return generateTokenPair(user, successionId);

    }

    private String generateSuccessionId() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < SUCCESSIONID_LENGTH; i++) {
            char chr = (char) (random.nextInt(94) + 33);
            sb.append(chr);
        }
        return sb.toString();
    }

    private Jwt generateAccessToken(String subjectName) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("simonkoo")
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_DURATION_IN_MINUTES, ChronoUnit.MINUTES))
                .subject(subjectName)
                .claim("type", "access")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }


    private Jwt generateRefreshToken(String subjectName, String successionId) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("simonkoo")
                .issuedAt(now)
                .expiresAt(now.plus(REFRESH_TOKEN_DURATION_IN_DAYS, ChronoUnit.DAYS))
                .subject(subjectName)
                .claim("type", "refresh")
                .claim("successionId", successionId)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    public static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}