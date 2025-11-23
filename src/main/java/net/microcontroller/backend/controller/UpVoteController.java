package net.microcontroller.backend.controller;

import net.microcontroller.backend.service.UpVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upvotes")
@RequiredArgsConstructor
public class UpVoteController {

	private UpVoteService upVoteService;

	@PostMapping("/toggle")
	public ResponseEntity<?> toggleUpVote(@RequestBody Map<String, String> request) {
		try {
			UUID projectId = UUID.fromString(request.get("projectId"));
			UUID userId = UUID.fromString(request.get("userId"));

			upVoteService.toggleUpVote(projectId, userId);
			boolean hasUpVoted = upVoteService.hasUserUpVoted(projectId, userId);
			long count = upVoteService.getUpVoteCount(projectId);

			return ResponseEntity.ok(Map.of(
					"hasUpVoted", hasUpVoted,
					"upVoteCount", count
			));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/project/{projectId}/user/{userId}")
	public ResponseEntity<?> checkUpVote(@PathVariable UUID projectId, @PathVariable UUID userId) {
		try {
			boolean hasUpVoted = upVoteService.hasUserUpVoted(projectId, userId);
			long count = upVoteService.getUpVoteCount(projectId);

			return ResponseEntity.ok(Map.of(
					"hasUpVoted", hasUpVoted,
					"upVoteCount", count
			));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		}
	}
}


