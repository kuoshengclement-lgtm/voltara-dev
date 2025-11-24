package net.microcontroller.backend.config;

import lombok.RequiredArgsConstructor;
import net.microcontroller.backend.model.Role;
import net.microcontroller.backend.model.User;
import net.microcontroller.backend.repository.UserRepository;
import net.microcontroller.backend.security.jwt.AuthEntryPointJwt;
import net.microcontroller.backend.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private AuthEntryPointJwt jwtAuthenticationEntryPoint;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		/*
		   http
			.csrf(AbstractHttpConfigurer::disable)
			 .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
			.sessionManagement(session -> session
					.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession) // Prevent session fixation attacks
					.maximumSessions(2)
					.maxSessionsPreventsLogin(true)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, "/api/users/signIn", "/api/users/register").permitAll()
				.requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
				.anyRequest().permitAll()
					.anyRequest().authenticated()
			)
			.logout( logout -> logout
				.logoutSuccessUrl("/api/auth/signout")
				.deleteCookies("JSESSIONID")
			)
			.headers(headers -> headers.frameOptions(
					HeadersConfigurer.FrameOptionsConfig::sameOrigin));
		return http.build();
		*/

		http.csrf(AbstractHttpConfigurer::disable)
				.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
		.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()
						.requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN", "MEMBER")
						.requestMatchers( "/css/**", "/js/**", "/images/**").permitAll()
				.anyRequest().permitAll()
		)
		.authenticationProvider(authenticationProvider())
		.sessionManagement(session -> session
				.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession) // Prevent session fixation attacks
				.maximumSessions(1)
				.maxSessionsPreventsLogin(true)
		);
		http.headers(headers -> headers.frameOptions(
				HeadersConfigurer.FrameOptionsConfig::sameOrigin));

		return http.build();
	}




}

