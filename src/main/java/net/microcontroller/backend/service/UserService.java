package net.microcontroller.backend.service;

import net.microcontroller.backend.dto.UserDTO;
import net.microcontroller.backend.model.Role;
import net.microcontroller.backend.model.User;
import net.microcontroller.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private ModelMapper modelMapper;

	@Transactional
	public UserDTO createUser(String email, String password, Role role) {
		if (userRepository.existsByEmail(email)) {
			throw new RuntimeException("User with email " + email + " already exists");
		}

		User user = User.builder()
				.email(email)
				.password(passwordEncoder.encode(password))
				.role(role)
				.build();

		User savedUser = userRepository.save(user);
		return modelMapper.map(savedUser, UserDTO.class);
	}

	public UserDTO getUserById(UUID userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		return modelMapper.map(user, UserDTO.class);
	}

	public UserDTO getUserByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
		return modelMapper.map(user, UserDTO.class);
	}

	public List<UserDTO> getAllUsers() {
		return userRepository.findAll().stream()
				.map(user -> modelMapper.map(user, UserDTO.class))
				.collect(Collectors.toList());
	}

	public User findUserEntityById(UUID userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
	}

	public User findUserEntityByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	}

	public UserDTO signIn(String email, String password) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Invalid email or password"));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new RuntimeException("Invalid email or password");
		}

		return modelMapper.map(user, UserDTO.class);
	}
}


