package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    SessionService sessionService;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    MessageRepository messageRepository;

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("@sessionService.isUserInSession(authentication.getName(), #sessionId)")
    public List<Message> messages(
            @PathVariable long sessionId,
            @RequestParam(name = "olderThan")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> timeStamp,

            @RequestParam(defaultValue = "10") int amount) {

        if (timeStamp.isPresent()) {
            return messageService.getMessagesBeforeTimestampForSession(sessionId, timeStamp.get(), amount);
        }

        return messageService.getLatestMessagesForSession(sessionId, amount);
    }

    //Testing only
    @PostMapping("/session/{sessionId}")
    @PreAuthorize("@sessionService.isUserInSession(authentication.getName(), #sessionId)")
    public Message sendMessage(@PathVariable long sessionId, @RequestParam String text,
                               @AuthenticationPrincipal(expression = "@userDetailsService.loadUserByUsername(#this.getSubject())") User user) {
        Message message = new Message(user, sessionRepository.findById(sessionId).orElseThrow(), text);
        return messageRepository.save(message);
    }

}
