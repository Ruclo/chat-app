package pat.mat.chat.app.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pat.mat.chat.app.dto.UserDTO;
import pat.mat.chat.app.model.User;
import pat.mat.chat.app.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("api/users/")
public class UserController {

    @Autowired
    private UserService userService;

    private boolean isValidImageMimeType(MultipartFile file) {
        String mimeType = file.getContentType();
        return mimeType != null && mimeType.startsWith("image/");
    }

    @PostMapping("/profilePicture")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @AuthenticationPrincipal(expression = "@userDetailsService.loadUserByUsername(#this.getSubject())") User user) {

        if (!isValidImageMimeType(file)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String fileUrl;
        try {
            fileUrl = userService.changeProfilePicture(file, user);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return fileUrl;
    }

}
