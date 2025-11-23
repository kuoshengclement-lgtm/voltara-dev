package net.microcontroller.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

public interface FileStorageService {
	void init();
	String store(MultipartFile file, UUID projectId);
	Resource loadAsResource(String filename);
	void deleteFile(String filename);
	Stream<Path> loadAll();
}


