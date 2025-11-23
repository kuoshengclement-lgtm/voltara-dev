package net.microcontroller.backend.config;

import net.microcontroller.backend.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileStorageConfig {

	@Autowired
	private FileStorageService fileStorageService;

	@PostConstruct
	public void init() {
		fileStorageService.init();
	}
}

