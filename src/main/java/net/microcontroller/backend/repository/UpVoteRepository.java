package net.microcontroller.backend.repository;

import net.microcontroller.backend.model.UpVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpVoteRepository extends JpaRepository<UpVote, UUID> {

	@Query("SELECT uv FROM UpVote uv WHERE uv.project.projectId = :projectId AND uv.user.userId = :userId")
	Optional<UpVote> findByProjectProjectIdAndUserUserId(UUID projectId, UUID userId);

	long countByProjectProjectId(UUID projectId);
	boolean existsByProjectProjectIdAndUserUserId(UUID projectId, UUID userId);
}


