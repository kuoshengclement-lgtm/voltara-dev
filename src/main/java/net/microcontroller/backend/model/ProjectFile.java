package net.microcontroller.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProjectFile {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "file_id")
	private UUID fileId;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "file_type", nullable = false)
	private String fileType; // "text" or image MIME type (image/png, image/jpeg, etc.)

	@Column(name = "file_path", nullable = false)
	private String filePath; // Path where file is stored

	@Column(name = "file_size")
	private Long fileSize; // File size in bytes

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}


