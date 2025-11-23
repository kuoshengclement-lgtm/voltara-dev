package net.microcontroller.backend.service;

import net.microcontroller.backend.dto.CommentDTO;
import net.microcontroller.backend.model.Comment;
import net.microcontroller.backend.model.Project;
import net.microcontroller.backend.model.User;
import net.microcontroller.backend.repository.CommentRepository;
import net.microcontroller.backend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

	private CommentRepository commentRepository;
	private ProjectRepository projectRepository;
	private UserService userService;
	private ModelMapper modelMapper;

	@Transactional
	public CommentDTO createComment(UUID projectId, UUID userId, String commentText) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
		User user = userService.findUserEntityById(userId);

		Comment comment = Comment.builder()
				.commentText(commentText)
				.project(project)
				.user(user)
				.build();

		Comment savedComment = commentRepository.save(comment);
		return convertToDTO(savedComment);
	}

	@Transactional
	public void deleteComment(UUID commentId, UUID userId) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

		if (!comment.getUser().getUserId().equals(userId)) {
			throw new RuntimeException("User is not authorized to delete this comment");
		}

		commentRepository.delete(comment);
	}

	public List<CommentDTO> getCommentsByProjectId(UUID projectId) {
		return commentRepository.findByProjectProjectId(projectId).stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<CommentDTO> getCommentsByUserId(UUID userId) {
		return commentRepository.findByUserId(userId).stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	private CommentDTO convertToDTO(Comment comment) {
		return CommentDTO.builder()
				.commentId(comment.getCommentId())
				.commentText(comment.getCommentText())
				.projectId(comment.getProject().getProjectId())
				.userId(comment.getUser().getUserId())
				.userEmail(comment.getUser().getEmail())
				.createdAt(comment.getCreatedAt())
				.build();
	}
}


