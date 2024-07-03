package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@SpringBootApplication
public class ChatappApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private SessionRepository sessionRepository;


	public static void main(String[] args) {
		SpringApplication.run(ChatappApplication.class, args);
	}

	@Override
	public void run(String... args) {
		User user = new User();

		user.setUsername("Simco");
		user.setPasswordHash("abcd");

		user = userRepository.save(user);

		Session session = new Session();


		session.setDateCreated(new Date());

		Set<User> users = new HashSet<>();
		users.add(user);

		session.setUsers(users);

		session = sessionRepository.save(session);

		Message message = new Message();

		message.setSession(session);

		message.setUser(user);
		message.setTimeStamp(new Date());

		message.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		messageRepository.save(message);
	}

	private void saveSession() {



	}




}
