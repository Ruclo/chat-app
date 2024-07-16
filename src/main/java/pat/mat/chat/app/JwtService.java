package pat.mat.chat.app;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class JwtService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Value("${rsa.private-key}")
    RSAPrivateKey privateKey;

    @Value("${rsa.public-key}")
    RSAPublicKey publicKey;

    @Autowired
    PersistentTokenRepository persistentTokenRepository;

    public static final int REFRESH_TOKEN_DURATION_IN_DAYS = 30;

    public static final int ACCESS_TOKEN_DURATION_IN_MINUTES = 15;

    public static final int REFRESH_TOKEN_DURATION_IN_SECONDS = REFRESH_TOKEN_DURATION_IN_DAYS * 24 * 60 * 60;

    public static final int SUCCESSIONID_LENGTH = 16;

    @Transactional(Transactional.TxType.SUPPORTS)
    public TokenPair generateTokenPair(User user) {
        String successionId = generateSuccessionId();
        String refreshTokenString = generateRefreshToken(user.getUsername(), successionId);
        System.out.println(successionId);
        PersistentToken refreshToken = new PersistentToken(successionId, refreshTokenString, user);
        persistentTokenRepository.save(refreshToken);

        return new TokenPair(generateAccessToken(user.getUsername()), refreshToken.getToken());
    }

    public TokenPair rotateAndRefreshTokens(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);
        String successionId = jwt.getClaimAsString("successionId");
        String subjectName = jwt.getSubject();

        PersistentToken persistentToken = persistentTokenRepository.findById(successionId).orElseThrow(PotentialCookieTheftException::new);

        if (!persistentToken.getToken().equals(refreshToken)) {
            throw new PotentialCookieTheftException();
        }
        persistentToken.setToken(generateRefreshToken(subjectName, successionId));
        persistentTokenRepository.save(persistentToken);

        return new TokenPair(generateAccessToken(subjectName), persistentToken.getToken());
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


    private String generateAccessToken(String subjectName) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("simonkoo")
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_DURATION_IN_MINUTES, ChronoUnit.MINUTES))
                .subject(subjectName)
                .claim("type", "access")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


    private String generateRefreshToken(String subjectName, String successionId) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("simonkoo")
                .issuedAt(now)
                .expiresAt(now.plus(REFRESH_TOKEN_DURATION_IN_DAYS, ChronoUnit.DAYS))
                .subject(subjectName)
                .claim("type", "refresh")
                .claim("successionId", successionId)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}