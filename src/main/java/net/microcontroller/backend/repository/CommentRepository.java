package net.microcontroller.backend.repository;

import net.microcontroller.backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
	@Query("SELECT c FROM Comment c WHERE c.project.projectId = :projectId")
	List<Comment> findByProjectProjectId(UUID projectId);

	@Query("SELECT c FROM Comment c WHERE c.user.userId = :userId")
	List<Comment> findByUserId(UUID userId);
}


