package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    SessionService sessionService;


    @PostMapping("/new")
    public long createNewSession(@AuthenticationPrincipal(expression = "@userDetailsService.loadUserByUsername(#this.getSubject())") User user,
                                 @RequestParam String participantUsername) {
        Session session;
        try { session = sessionService.createNewSession(user, participantUsername); }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return session.getId();

    }

    @PostMapping("/{sessionId}/addUser")
    @PreAuthorize("@sessionService.isUserInSession(authentication.getName(), #sessionId)")
    public void addUser(@PathVariable("sessionId") long sessionId, @RequestParam String userToAdd) {
        try { sessionService.addUserToSession(sessionId, userToAdd); }
        catch (Exception e) {
            System.out.println(e.getMessage());
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
