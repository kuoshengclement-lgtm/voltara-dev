package net.microcontroller.backend.dto;

import net.microcontroller.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	private UUID userId;
	private String email;
	private Role role;
	private LocalDateTime createdAt;

}


