package pat.mat.chat.app;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;


@RestController
@RequestMapping("/auth")
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
    public void register(@RequestBody @Valid UserDTO userDto, HttpServletResponse httpServletResponse) {


        TokenPair tokenPair;
        try {tokenPair = authService.registerUser(userDto);}
        catch (UserAlreadyExistsException uaee) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        addAuthCookiesToResponse(httpServletResponse, tokenPair);
    }

    @PostMapping("/login")
    public void login(@RequestBody @Valid UserDTO userDto, HttpServletResponse httpServletResponse) {

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

        addAuthCookiesToResponse(httpServletResponse, tokenPair);
    }

    @GetMapping("/refresh")
    public void refresh(@CookieValue("refresh_token") String refreshToken, HttpServletResponse httpServletResponse) {
        TokenPair tokenPair;

        try {tokenPair = authService.refreshTokens(refreshToken);}
        catch (PotentialCookieTheftException pcte) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cookie theft detected");
        }

        addAuthCookiesToResponse(httpServletResponse, tokenPair);
    }

    @PostMapping("/logout")
    public void logout(@CookieValue("refresh_token") String refreshToken, HttpServletResponse httpServletResponse) {

        try {authService.logOut(refreshToken);}
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        destroyAuthCookies(httpServletResponse);
    }

    private void addAuthCookiesToResponse(HttpServletResponse httpServletResponse, TokenPair tokenPair) {
        _addCookiesToResponse(httpServletResponse, tokenPair,
                Duration.ofDays(JwtService.REFRESH_TOKEN_DURATION_IN_DAYS),
                Duration.ofMinutes(JwtService.ACCESS_TOKEN_DURATION_IN_MINUTES));

    }

    private void destroyAuthCookies(HttpServletResponse httpServletResponse) {
        TokenPair tokenPair = new TokenPair("", "");
        _addCookiesToResponse(httpServletResponse, tokenPair, Duration.ofSeconds(0), Duration.ofSeconds(0));
    }

    private void _addCookiesToResponse(HttpServletResponse httpServletResponse, TokenPair tokenPair, Duration refreshMaxAge, Duration accessMaxAge) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", tokenPair.refreshToken())
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshMaxAge)
                .sameSite("None")
                .domain("127.0.0.1")
                .path("/auth")
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", tokenPair.accessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain("127.0.0.1")
                .path("/")
                .maxAge(accessMaxAge)
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    }

}
