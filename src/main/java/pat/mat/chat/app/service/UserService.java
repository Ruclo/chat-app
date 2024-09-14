package pat.mat.chat.app.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pat.mat.chat.app.dto.UserDTO;
import pat.mat.chat.app.model.User;
import pat.mat.chat.app.repository.UserRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String changeProfilePicture(MultipartFile file, User user) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "pfps"));

        if (!user.getPfpUrl().equals(User.DEFAULT_PFP_URL)) {
            String currentPfpUrl = user.getPfpUrl();
            String publicId = currentPfpUrl.substring(currentPfpUrl.indexOf("pfps/"));
            cloudinary.uploader().destroy(publicId, Collections.emptyMap());
        }
        String imageUrl = (String) uploadResult.get("secure_url");

        user.setPfpUrl(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    public List<UserDTO> getUsersBySessionId(long sessionId, int page, int count) {
        Pageable pageable = PageRequest.of(page, count);
        List<User> users = userRepository.findUsersBySessionId(sessionId, pageable);
        return users.stream().
                map(UserDTO::new).toList();
    }
}
