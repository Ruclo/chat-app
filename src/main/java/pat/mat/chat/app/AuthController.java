package pat.mat.chat.app;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;



@RestController
public class AuthController {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    AuthService authService;


    @GetMapping("/test")
    public String test() {
        return "Success";
    }

    @PostMapping("/register")
    public AuthResponse register(@ModelAttribute @Valid UserDTO userDto, HttpServletResponse httpServletResponse) {


        TokenPair tokenPair;
        try {tokenPair = authService.registerUser(userDto);}
        catch (UserAlreadyExistsException uaee) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Cookie persistentTokenCookie = new Cookie("refreshToken", tokenPair.refreshToken());
        persistentTokenCookie.setHttpOnly(true);
        persistentTokenCookie.setSecure(true);
        persistentTokenCookie.setMaxAge(JwtService.REFRESH_TOKEN_DURATION_IN_SECONDS);
        httpServletResponse.addCookie(persistentTokenCookie);


        return new AuthResponse(tokenPair.accessToken());
    }

    @PostMapping("/login")
    public AuthResponse login(@ModelAttribute @Valid UserDTO userDto, HttpServletResponse httpServletResponse) {

        Authentication authentication;
        
        try {authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));}
        catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);


        TokenPair tokenPair;
        try {tokenPair = authService.loginUser(userDto);}
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Cookie persistentTokenCookie = new Cookie("refreshToken", tokenPair.refreshToken());
        persistentTokenCookie.setHttpOnly(true);
        persistentTokenCookie.setSecure(true);
        persistentTokenCookie.setMaxAge(JwtService.REFRESH_TOKEN_DURATION_IN_SECONDS);
        httpServletResponse.addCookie(persistentTokenCookie);


        return new AuthResponse(tokenPair.accessToken());    }

    @GetMapping("/refresh")
    public AuthResponse refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse httpServletResponse) {
        TokenPair tokenPair;
        System.out.println(refreshToken);
        try {tokenPair = authService.refreshTokens(refreshToken);}
        catch (PotentialCookieTheftException pcte) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cookie theft detected");
        }

        Cookie persistentTokenCookie = new Cookie("refreshToken", tokenPair.refreshToken());
        persistentTokenCookie.setHttpOnly(true);
        persistentTokenCookie.setSecure(true);
        persistentTokenCookie.setMaxAge(JwtService.REFRESH_TOKEN_DURATION_IN_SECONDS);
        httpServletResponse.addCookie(persistentTokenCookie);

        return new AuthResponse(tokenPair.accessToken());
    }

    @PostMapping("/logout")
    public void logout(@CookieValue("refreshToken") String refreshToken, HttpServletResponse httpServletResponse) {

        try {authService.logOut(refreshToken);}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Cookie persistentTokenCookie = new Cookie("refreshToken", null);
        persistentTokenCookie.setHttpOnly(true);
        persistentTokenCookie.setSecure(true);
        persistentTokenCookie.setMaxAge(0);
        httpServletResponse.addCookie(persistentTokenCookie);

    }

}
