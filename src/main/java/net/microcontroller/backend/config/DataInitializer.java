package net.microcontroller.backend.config;

import net.microcontroller.backend.model.Category;
import net.microcontroller.backend.model.Role;
import net.microcontroller.backend.model.User;
import net.microcontroller.backend.repository.CategoryRepository;
import net.microcontroller.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		// Initialize categories
		if (categoryRepository.count() == 0.0) {
			categoryRepository.save(Category.builder().name("Arduino").description("Arduino microcontroller projects").build());
			categoryRepository.save(Category.builder().name("Raspberry Pi").description("Raspberry Pi projects").build());
			categoryRepository.save(Category.builder().name("ESP32").description("ESP32 microcontroller projects").build());
			categoryRepository.save(Category.builder().name("Orange Pi").description("Orange Pi projects").build());
		}

		// Initialize admin user
		if (!userRepository.existsByEmail("admin@microcontroller.com")) {
			userRepository.save(User.builder()
					.email("admin@microcontroller.com")
					.password(passwordEncoder.encode("admin123"))
					.role(Role.ADMINISTRATOR)
					.createdAt(LocalDateTime.now())
					.build());
		}
	}
}

