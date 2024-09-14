package pat.mat.chat.app.config;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Autowired
    private Dotenv dotenv;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    }
}
