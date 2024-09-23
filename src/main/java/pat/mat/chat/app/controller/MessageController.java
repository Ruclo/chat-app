package pat.mat.chat.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pat.mat.chat.app.dto.MessageDTO;
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
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<Instant> olderThanTimeStamp,

            @RequestParam(name = "newerThan")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<Instant> newerThanTimeStamp,

            @RequestParam(defaultValue = "10") int amount) {


        if (olderThanTimeStamp.isPresent()) {
            return messageService.getMessagesBeforeTimestampForSession(sessionId, olderThanTimeStamp.get(), amount);
        }

        if (newerThanTimeStamp.isPresent()) {
            return messageService.getAllMessagesAfterTimestampForSession(sessionId, newerThanTimeStamp.get());
        }

        return messageService.getLatestMessagesForSession(sessionId, amount);
    }

}
