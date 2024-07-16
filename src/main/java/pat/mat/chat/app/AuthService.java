package pat.mat.chat.app;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    JwtService jwtService;

    @Autowired
    PersistentTokenRepository persistentTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtDecoder jwtDecoder;

    @Transactional
    public TokenPair registerUser(User user) {
        userRepository.save(user);
        return jwtService.generateTokenPair(user);
    }

    public TokenPair loginUser(User user) {
        return jwtService.generateTokenPair(user);
    }

    @Transactional
    public TokenPair refreshTokens(String refreshToken) {
        TokenPair tokenPair;
        try {tokenPair = jwtService.rotateAndRefreshTokens(refreshToken);}
        catch(PotentialCookieTheftException ptce) {
            persistentTokenRepository.deleteByUserUsername(jwtDecoder.decode(refreshToken).getSubject());
            throw ptce;
        }

        return tokenPair;
    }

}
