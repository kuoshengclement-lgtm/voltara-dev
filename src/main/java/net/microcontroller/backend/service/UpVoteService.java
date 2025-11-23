package net.microcontroller.backend.service;

import net.microcontroller.backend.model.Project;
import net.microcontroller.backend.model.UpVote;
import net.microcontroller.backend.model.User;
import net.microcontroller.backend.repository.ProjectRepository;
import net.microcontroller.backend.repository.UpVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpVoteService {

	private UpVoteRepository upVoteRepository;
	private ProjectRepository projectRepository;
	private UserService userService;

	@Transactional
	public void toggleUpVote(UUID projectId, UUID userId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
		User user = userService.findUserEntityById(userId);

		upVoteRepository.findByProjectProjectIdAndUserUserId(projectId, userId)
				.ifPresentOrElse(
						upVote -> upVoteRepository.delete(upVote), // Remove upvote if exists
						() -> { // Add upvote if doesn't exist
							UpVote upVote = UpVote.builder()
									.project(project)
									.user(user)
									.build();
							upVoteRepository.save(upVote);
						}
				);
	}

	public boolean hasUserUpVoted(UUID projectId, UUID userId) {
		return upVoteRepository.existsByProjectProjectIdAndUserUserId(projectId, userId);
	}

	public long getUpVoteCount(UUID projectId) {
		return upVoteRepository.countByProjectProjectId(projectId);
	}
}


