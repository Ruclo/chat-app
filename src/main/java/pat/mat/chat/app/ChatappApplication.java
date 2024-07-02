package pat.mat.chat.app;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ChatappApplication implements CommandLineRunner {

	@Autowired
	private UserCrudRepository userCrudRepository;

	public static void main(String[] args) {
		SpringApplication.run(ChatappApplication.class, args);
	}

	@Override
	public void run(String... args) {
		saveUser();
	}

	private void saveUser() {

		User user = new User();

		user.setUsername("Simco");
		user.setPasswordHash("abcd");

		userCrudRepository.save(user);

	}



}
