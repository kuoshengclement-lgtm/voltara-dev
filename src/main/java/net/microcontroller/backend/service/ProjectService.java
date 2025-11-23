package net.microcontroller.backend.service;

import net.microcontroller.backend.dto.ProjectDTO;
import net.microcontroller.backend.dto.ProjectFileDTO;
import net.microcontroller.backend.model.Project;
import net.microcontroller.backend.model.ProjectFile;
import net.microcontroller.backend.repository.ProjectRepository;
import net.microcontroller.backend.repository.UpVoteRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private ProjectRepository projectRepository;
	private UserService userService;
	private CategoryService categoryService;
	private FileStorageService fileStorageService;
	private UpVoteRepository upVoteRepository;
	private ModelMapper modelMapper;

	@Transactional
	public ProjectDTO createProject(UUID userId, UUID categoryId, String title, String description, List<MultipartFile> files) {
		Project project = Project.builder()
				.title(title)
				.description(description)
				.user(userService.findUserEntityById(userId))
				.category(categoryService.findCategoryEntityById(categoryId))
				.build();

		Project savedProject = projectRepository.save(project);

		// Save files
		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					String filePath = fileStorageService.store(file, savedProject.getProjectId());
					ProjectFile projectFile = ProjectFile.builder()
							.fileName(file.getOriginalFilename())
							.fileType(file.getContentType())
							.filePath(filePath)
							.fileSize(file.getSize())
							.project(savedProject)
							.build();
					savedProject.getFiles().add(projectFile);
				}
			}
			savedProject = projectRepository.save(savedProject);
		}

		return convertToDTO(savedProject);
	}

	@Transactional
	public ProjectDTO updateProject(UUID projectId, UUID userId, String title, String description, List<MultipartFile> files) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

		if (!project.getUser().getUserId().equals(userId)) {
			throw new RuntimeException("User is not authorized to update this project");
		}

		if (title != null) project.setTitle(title);
		if (description != null) project.setDescription(description);

		// Add new files
		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					String filePath = fileStorageService.store(file, project.getProjectId());
					ProjectFile projectFile = ProjectFile.builder()
							.fileName(file.getOriginalFilename())
							.fileType(file.getContentType())
							.filePath(filePath)
							.fileSize(file.getSize())
							.project(project)
							.build();
					project.getFiles().add(projectFile);
				}
			}
		}

		Project updatedProject = projectRepository.save(project);
		return convertToDTO(updatedProject);
	}

	@Transactional
	public void deleteProject(UUID projectId, UUID userId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

		if (!project.getUser().getUserId().equals(userId)) {
			throw new RuntimeException("User is not authorized to delete this project");
		}

		// Delete associated files
		for (ProjectFile file : project.getFiles()) {
			fileStorageService.deleteFile(file.getFilePath());
		}

		projectRepository.delete(project);
	}

	public ProjectDTO getProjectById(UUID projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
		return convertToDTO(project);
	}

	public List<ProjectDTO> getAllProjects() {
		return projectRepository.findAll().stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<ProjectDTO> getProjectsByUserId(UUID userId) {
		return projectRepository.findByUserUserId(userId).stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<ProjectDTO> getProjectsByCategory(UUID categoryId) {
		return projectRepository.findByCategoryCategoryId(categoryId).stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	public List<ProjectDTO> searchProjects(String keyword, String categoryName) {
		List<Project> projects;
		if (categoryName != null && !categoryName.isEmpty()) {
			if (keyword != null && !keyword.isEmpty()) {
				projects = projectRepository.searchByCategoryAndKeyword(categoryName, keyword);
			} else {
				projects = projectRepository.findByCategoryName(categoryName);
			}
		} else {
			if (keyword != null && !keyword.isEmpty()) {
				projects = projectRepository.searchByKeyword(keyword);
			} else {
				projects = projectRepository.findAll();
			}
		}
		return projects.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	private ProjectDTO convertToDTO(Project project) {
		ProjectDTO dto = ProjectDTO.builder()
				.projectId(project.getProjectId())
				.title(project.getTitle())
				.description(project.getDescription())
				.categoryId(project.getCategory().getCategoryId())
				.categoryName(project.getCategory().getName())
				.userId(project.getUser().getUserId())
				.userEmail(project.getUser().getEmail())
				.createdAt(project.getCreatedAt())
				.updatedAt(project.getUpdatedAt())
				.commentCount((long) project.getComments().size())
				.upVoteCount(upVoteRepository.countByProjectProjectId(project.getProjectId()))
				.build();

		// Convert files
		List<ProjectFileDTO> fileDTOs = project.getFiles().stream()
				.map(file -> modelMapper.map(file, ProjectFileDTO.class))
				.collect(Collectors.toList());
		dto.setFiles(fileDTOs);

		return dto;
	}
}


