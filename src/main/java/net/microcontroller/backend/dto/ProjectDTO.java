package net.microcontroller.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {
	private UUID projectId;
	private String title;
	private String description;
	private UUID categoryId;
	private String categoryName;
	private UUID userId;
	private String userEmail;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<ProjectFileDTO> files;
	private Long commentCount;
	private Long upVoteCount;


}


