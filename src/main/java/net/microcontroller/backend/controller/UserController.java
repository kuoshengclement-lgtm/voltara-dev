package net.microcontroller.backend.controller;

import net.microcontroller.backend.dto.UserDTO;
import net.microcontroller.backend.model.Role;
import net.microcontroller.backend.security.response.UserInfoResponse;
import net.microcontroller.backend.security.service.UserDetailsImpl;
import net.microcontroller.backend.security.jwt.JwtUtils;
import net.microcontroller.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
	//public ResponseEntity<?> registerUser(@RequestBody LoginRequest request) {
		try {
			String email = request.get("email");
			String password = request.get("password");
			Role role = request.containsKey("role") ? Role.valueOf(request.get("role")) : Role.MEMBER;


			UserDTO user = userService.createUser(email, password,  role);
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
		Authentication authentication;
		String email = request.get("email");
		String password = request.get("password");
		try {

			System.out.println("email: " + email);
			System.out.println("password: " + password);
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			if (email == null || password == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "Email a" +
								"nd password are required"));
			}


		} catch (Exception e) {
			Map<String, Object> map = new HashMap<>();
			map.put("message", "Bad credentials in Signin");
			map.put("status", false);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", e.getMessage()));
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userCookieDetails = (UserDetailsImpl) authentication.getPrincipal();
		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userCookieDetails);
		String token = jwtCookie.getValue();
		List<String> roles = userCookieDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		UserDTO loginUser = userService.signIn(email, password);
		UserInfoResponse infoResponse = new UserInfoResponse(loginUser.getEmail(), roles, token);
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,token)
				.body(infoResponse);
	}
}


