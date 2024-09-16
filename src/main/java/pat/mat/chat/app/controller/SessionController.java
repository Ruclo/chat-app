package pat.mat.chat.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pat.mat.chat.app.dto.SessionCreationDTO;
import pat.mat.chat.app.dto.SessionDTO;
import pat.mat.chat.app.dto.UserDTO;
import pat.mat.chat.app.service.SessionService;
import pat.mat.chat.app.model.User;
import pat.mat.chat.app.service.UserService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @Autowired
    UserService userService;

    @GetMapping
    public List<SessionDTO> getSessions(@AuthenticationPrincipal Jwt jwt,
                                        @RequestParam(required = false) Instant timestamp, @RequestParam(defaultValue = "10") int count) {
        return sessionService.getUsersSessionsOrdered(jwt.getSubject(), timestamp, count);
    }

    @PreAuthorize("@sessionService.isUserInSession(authentication.getName(), #sessionId)")
    @GetMapping("/{sessionId}/members")
    public List<UserDTO> getMembers(@PathVariable("sessionId") long sessionId,
                                    @RequestParam(required = false, defaultValue = "0") int page,
                                    @RequestParam(required = false, defaultValue = "20") int count) {
        return userService.getUsersBySessionId(sessionId, page, count);
    }

    @PostMapping("/")
    public SessionDTO createNewSession(@AuthenticationPrincipal(expression = "@userDetailsService.loadUserByUsername(#this.getSubject())") User user,
                                 @RequestBody @Validated SessionCreationDTO sessionCreationDTO) {
        SessionDTO session;

        try { session = sessionService.createNewSession(user, sessionCreationDTO); }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return session;

    }

    @PostMapping("/{sessionId}/user/{username}")
    @PreAuthorize("@sessionService.isUserInSession(authentication.getName(), #sessionId)")
    public void addUser(@PathVariable("sessionId") long sessionId, @PathVariable("username") String usernameToAdd) {
        try { sessionService.addUserToSession(sessionId, usernameToAdd); }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{sessionId}/leave")
    public void leaveSession(@PathVariable("sessionId") long sessionId, @AuthenticationPrincipal(expression = "@userDetailsService.loadUserByUsername(#this.getSubject())") User user) {
        try { sessionService.removeUser(sessionId, user); }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }


}
