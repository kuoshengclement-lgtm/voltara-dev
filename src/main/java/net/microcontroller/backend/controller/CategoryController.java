package net.microcontroller.backend.controller;

import lombok.RequiredArgsConstructor;
import net.microcontroller.backend.dto.CategoryDTO;
import net.microcontroller.backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private CategoryService categoryService;

	@PostMapping("/create")
	public ResponseEntity<?> createCategory(@RequestBody Map<String, String> request) {
		try {
			String name = request.get("name");
			String description = request.getOrDefault("description", "");

			CategoryDTO category = categoryService.createCategory(name, description);
			return ResponseEntity.status(HttpStatus.CREATED).body(category);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/getlist")
	public ResponseEntity<List<CategoryDTO>> getAllCategories() {
		List<CategoryDTO> categories = categoryService.getAllCategories();
		return ResponseEntity.ok(categories);
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<?> getCategoryById(@PathVariable UUID categoryId) {
		try {
			CategoryDTO category = categoryService.getCategoryById(categoryId);
			return ResponseEntity.ok(category);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", e.getMessage()));
		}
	}
}


