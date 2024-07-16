package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ChatappApplication {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private SessionRepository sessionRepository;


	public static void main(String[] args) {
		SpringApplication.run(ChatappApplication.class, args);
	}







}
