package pat.mat.chat.app.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
import pat.mat.chat.app.dto.AuthUserDTO;
import pat.mat.chat.app.dto.UserDTO;
import pat.mat.chat.app.exception.PotentialCookieTheftException;
import pat.mat.chat.app.exception.UserAlreadyExistsException;
import pat.mat.chat.app.model.User;
import pat.mat.chat.app.service.AuthService;
import pat.mat.chat.app.service.JwtService;
import pat.mat.chat.app.util.TokenPair;

import java.time.Duration;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    AuthService authService;

    @Autowired
    JwtService jwtService;

    @Transactional
    @PostMapping("/register")
    public UserDTO register(@RequestBody @Valid AuthUserDTO authUserDto, HttpServletResponse httpServletResponse) {

        TokenPair tokenPair;
        User user;
        try {user = authService.registerUser(authUserDto);}
        catch (UserAlreadyExistsException uaee) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {tokenPair = jwtService.generateTokenPair(user);}
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        addAuthCookiesToResponse(httpServletResponse, tokenPair);

        return new UserDTO(user);
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody @Valid AuthUserDTO authUserDto, HttpServletResponse httpServletResponse) {

        Authentication authentication;
        
        try {authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(authUserDto.getUsername(), authUserDto.getPassword()));}
        catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);


        TokenPair tokenPair;
        User user;
        try {user = authService.loginUser(authUserDto);}
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {tokenPair = jwtService.generateTokenPair(user);}
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        addAuthCookiesToResponse(httpServletResponse, tokenPair);

        return new UserDTO(user);
    }

    @PostMapping("/refresh")
    public UserDTO refresh(@CookieValue("refresh_token") String refreshToken,
                           @CookieValue(name="access_token", required = false) String accessToken,
                           HttpServletResponse httpServletResponse) {
        TokenPair tokenPair;

        try {tokenPair = authService.refreshTokens(refreshToken, accessToken);}
        catch (PotentialCookieTheftException pcte) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cookie theft detected");
        }

        addAuthCookiesToResponse(httpServletResponse, tokenPair);

        User user = jwtService.getUserFromToken(tokenPair.accessToken());
        return new UserDTO(user);
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
                .sameSite("Strict")
                .path("/api/auth")
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", tokenPair.accessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api")
                .maxAge(accessMaxAge)
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    }

}
