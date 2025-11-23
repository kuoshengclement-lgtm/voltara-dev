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
public class ProjectFileDTO {
	private UUID fileId;
	private String fileName;
	private String fileType;
	private String filePath;
	private Long fileSize;
	private LocalDateTime createdAt;
}


