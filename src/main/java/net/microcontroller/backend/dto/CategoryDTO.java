package net.microcontroller.backend.dto;

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
public class CategoryDTO {
	private UUID categoryId;
	private String name;
	private String description;
	private LocalDateTime createdAt;
}


