package net.microcontroller.backend.controller;

import net.microcontroller.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

	private FileStorageService fileStorageService;

	@GetMapping("/{filename:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		try {
			Resource file = fileStorageService.loadAsResource(filename);
			String contentType = determineContentType(filename);

			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
					.body(file);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(null);
		}
	}

	private String determineContentType(String filename) {
		String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
		return switch (extension) {
			case "png" -> "image/png";
			case "jpg", "jpeg" -> "image/jpeg";
			case "bmp" -> "image/bmp";
			case "txt" -> "text/plain";
			default -> "application/octet-stream";
		};
	}
}


