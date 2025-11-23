package net.microcontroller.backend.controller;

import net.microcontroller.backend.dto.UserDTO;
import net.microcontroller.backend.model.Role;
import net.microcontroller.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private UserService userService;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
		try {
			String email = request.get("email");
			String password = request.get("password");
			Role role = request.containsKey("role") ? Role.valueOf(request.get("role")) : Role.MEMBER;

			UserDTO user = userService.createUser(email, password, role);
			return ResponseEntity.status(HttpStatus.CREATED).body(user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
		try {
			UserDTO user = userService.getUserById(userId);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<UserDTO> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	@PostMapping("/signIn")
	public ResponseEntity<?> signInUser(@RequestBody Map<String, String> request) {
		try {
			String email = request.get("email");
			String password = request.get("password");

			if (email == null || password == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "Email and password are required"));
			}

			UserDTO user = userService.signIn(email, password);
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", e.getMessage()));
		}
	}
}


