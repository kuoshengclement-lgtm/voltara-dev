package net.microcontroller.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "upvotes", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"project_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UpVote {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "upvote_id")
	private UUID upVoteId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;


}


