package pat.mat.chat.app.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import pat.mat.chat.app.dto.AuthUserDTO;
import pat.mat.chat.app.exception.PotentialCookieTheftException;
import pat.mat.chat.app.exception.UserAlreadyExistsException;
import pat.mat.chat.app.model.User;
import pat.mat.chat.app.repository.PersistentTokenRepository;
import pat.mat.chat.app.repository.UserRepository;
import pat.mat.chat.app.util.TokenPair;
import pat.mat.chat.app.websocket.WebSocketSessionRegistry;

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

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    WebSocketSessionRegistry webSocketSessionRegistry;

    @Transactional
    public User registerUser(AuthUserDTO authUserDto) {

        if (userRepository.findByUsername(authUserDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }
        User user = new User();
        user.setUsername(authUserDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(authUserDto.getPassword()));

        return userRepository.save(user);
    }

    public User loginUser(AuthUserDTO authUserDto) {

        return userRepository.findByUsername(authUserDto.getUsername()).orElseThrow();

    }

    @Transactional
    public TokenPair refreshTokens(String refreshToken, String accessToken) {
        TokenPair tokenPair;
        try {tokenPair = jwtService.rotateAndRefreshTokens(refreshToken);}
        catch(PotentialCookieTheftException ptce) {
            persistentTokenRepository.deleteByUserUsername(jwtDecoder.decode(refreshToken).getSubject());
            throw ptce;
        }

        if (accessToken != null) {
            webSocketSessionRegistry.updateSession(jwtDecoder.decode(accessToken),
                    jwtDecoder.decode(tokenPair.accessToken()));
        }
        return tokenPair;
    }

    public void logOut(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);

        String successionId = jwt.getClaimAsString("successionId");
        persistentTokenRepository.deleteById(successionId);
    }
}
