package pat.mat.chat.app;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    public TokenPair registerUser(UserDTO userDto) {

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("An user with this username already exists");
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(user);
        return jwtService.generateTokenPair(user);
    }

    public TokenPair loginUser(UserDTO userDto) {

        User user = userRepository.findByUsername(userDto.getUsername()).orElseThrow();
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

    public void logOut(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);

        String successionId = jwt.getClaimAsString("successionId");
        persistentTokenRepository.deleteById(successionId);
    }
}
