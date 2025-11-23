package net.microcontroller.backend.controller;

import net.microcontroller.backend.dto.ProjectDTO;
import net.microcontroller.backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

	private ProjectService projectService;

	@PostMapping("/create")
	public ResponseEntity<?> createProject(
			@RequestParam("userId") UUID userId,
			@RequestParam("categoryId") UUID categoryId,
			@RequestParam("title") String title,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "files", required = false) List<MultipartFile> files) {
		try {
			ProjectDTO project = projectService.createProject(userId, categoryId, title, description, files);
			return ResponseEntity.status(HttpStatus.CREATED).body(project);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/update/{projectId}")
	public ResponseEntity<?> updateProject(
			@PathVariable UUID projectId,
			@RequestParam("userId") UUID userId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "files", required = false) List<MultipartFile> files) {
		try {
			ProjectDTO project = projectService.updateProject(projectId, userId, title, description, files);
			return ResponseEntity.ok(project);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/delete/{projectId}")
	public ResponseEntity<?> deleteProject(
			@PathVariable UUID projectId,
			@RequestParam("userId") UUID userId) {
		try {
			projectService.deleteProject(projectId, userId);
			return ResponseEntity.ok(Map.of("message", "Project deleted successfully"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/list")
	public ResponseEntity<List<ProjectDTO>> getAllProjects(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "category", required = false) String categoryName) {
		List<ProjectDTO> projects = projectService.searchProjects(keyword, categoryName);
		return ResponseEntity.ok(projects);
	}

	@GetMapping("/get/{projectId}")
	public ResponseEntity<?> getProjectById(@PathVariable UUID projectId) {
		try {
			ProjectDTO project = projectService.getProjectById(projectId);
			return ResponseEntity.ok(project);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/get/{userId}")
	public ResponseEntity<List<ProjectDTO>> getProjectsByUserId(@PathVariable UUID userId) {
		List<ProjectDTO> projects = projectService.getProjectsByUserId(userId);
		return ResponseEntity.ok(projects);
	}

	@GetMapping("/get/{categoryId}")
	public ResponseEntity<List<ProjectDTO>> getProjectsByCategory(@PathVariable UUID categoryId) {
		List<ProjectDTO> projects = projectService.getProjectsByCategory(categoryId);
		return ResponseEntity.ok(projects);
	}
}


