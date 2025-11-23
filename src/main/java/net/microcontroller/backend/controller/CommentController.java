package net.microcontroller.backend.controller;

import lombok.RequiredArgsConstructor;
import net.microcontroller.backend.dto.CommentDTO;
import net.microcontroller.backend.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private CommentService commentService;

	@PostMapping("/create")
	public ResponseEntity<?> createComment(@RequestBody Map<String, String> request) {
		try {
			UUID projectId = UUID.fromString(request.get("projectId"));
			UUID userId = UUID.fromString(request.get("userId"));
			String commentText = request.get("commentText");

			CommentDTO comment = commentService.createComment(projectId, userId, commentText);
			return ResponseEntity.status(HttpStatus.CREATED).body(comment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> deleteComment(
			@PathVariable UUID commentId,
			@RequestParam("userId") UUID userId) {
		try {
			commentService.deleteComment(commentId, userId);
			return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<CommentDTO>> getCommentsByProjectId(@PathVariable UUID projectId) {
		List<CommentDTO> comments = commentService.getCommentsByProjectId(projectId);
		return ResponseEntity.ok(comments);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<CommentDTO>> getCommentsByUserId(@PathVariable UUID userId) {
		List<CommentDTO> comments = commentService.getCommentsByUserId(userId);
		return ResponseEntity.ok(comments);
	}
}


