package net.microcontroller.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

	private final Path rootLocation;
	private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
			"image/png", "image/jpeg", "image/jpg", "image/bmp"
	);
	private static final List<String> ALLOWED_TEXT_TYPES = Arrays.asList(
			"text/plain", "text/x-c", "text/x-c++"
	);

	public FileStorageServiceImpl(@Value("${file.upload-dir:uploads}") String uploadDir) {
		this.rootLocation = Paths.get(uploadDir);
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize storage", e);
		}
	}

	@Override
	public String store(MultipartFile file, UUID projectId) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		if (file.isEmpty()) {
			throw new RuntimeException("Failed to store empty file " + filename);
		}
		if (filename.contains("..")) {
			throw new RuntimeException("Cannot store file with relative path outside current directory " + filename);
		}

		// Validate file type
		String contentType = file.getContentType();
		if (contentType == null || 
			(!ALLOWED_IMAGE_TYPES.contains(contentType) && !ALLOWED_TEXT_TYPES.contains(contentType))) {
			throw new RuntimeException("File type not allowed: " + contentType);
		}

		try {
			// Create project-specific directory
			Path projectDir = rootLocation.resolve(projectId.toString());
			Files.createDirectories(projectDir);

			// Generate unique filename
			String extension = filename.substring(filename.lastIndexOf("."));
			String uniqueFilename = UUID.randomUUID().toString() + extension;
			Path destinationFile = projectDir.resolve(uniqueFilename);

			Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
			return projectId.toString() + "/" + uniqueFilename;
		} catch (IOException e) {
			throw new RuntimeException("Failed to store file " + filename, e);
		}
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = rootLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteFile(String filename) {
		try {
			Path file = rootLocation.resolve(filename);
			Files.deleteIfExists(file);
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete file: " + filename, e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
					.filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read stored files", e);
		}
	}
}


