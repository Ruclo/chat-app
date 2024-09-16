package pat.mat.chat.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pat.mat.chat.app.dto.MessageDTO;
import pat.mat.chat.app.model.Message;
import pat.mat.chat.app.model.User;
import pat.mat.chat.app.repository.MessageRepository;
import pat.mat.chat.app.repository.SessionRepository;
import pat.mat.chat.app.service.MessageService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    MessageService messageService;

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("@sessionService.isUserInSession(authentication.getName(), #sessionId)")
    public List<MessageDTO> messages(
            @PathVariable long sessionId,
            @RequestParam(name = "olderThan")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<Instant> timeStamp,
            @RequestParam(defaultValue = "10") int amount) {

        List<MessageDTO> result;

        if (timeStamp.isPresent()) {
            result = messageService.getMessagesBeforeTimestampForSession(sessionId, timeStamp.get(), amount);
        } else {
            result = messageService.getLatestMessagesForSession(sessionId, amount);
        }

        return result;
    }

}
