package net.microcontroller.backend.config;

import net.microcontroller.backend.model.Category;
import net.microcontroller.backend.model.Role;
import net.microcontroller.backend.model.User;
import net.microcontroller.backend.repository.CategoryRepository;
import net.microcontroller.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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

			List<SimpleGrantedAuthority> adminAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
			User admin = new User("admin@microcontroller.com", passwordEncoder.encode("adminPa$s"), adminAuthorities);
			admin.setRoles(List.of(Role.ADMINISTRATOR));
			admin.setCreatedAt(LocalDateTime.now());
			userRepository.save(admin);
		}


		List<SimpleGrantedAuthority> userAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
		// Create users if not already present
		if (!userRepository.existsByEmail("kuosheng.ang@outlook.com")) {
			User user1 = new User("kuosheng.ang@outlook.com", passwordEncoder.encode("pas$wOrd6"), userAuthorities);
			user1.setRoles(List.of(Role.MEMBER));
			user1.setCreatedAt(LocalDateTime.now());
			userRepository.save(user1);
		}

		if (!userRepository.existsByEmail("user.demo@outlook.com")) {
			User user2 = new User("user.demo@outlook.com", passwordEncoder.encode("pas$wOrd1"), userAuthorities);
			user2.setCreatedAt(LocalDateTime.now());
			user2.setRoles(List.of(Role.MEMBER));
			userRepository.save(user2);
		}


	}


}

