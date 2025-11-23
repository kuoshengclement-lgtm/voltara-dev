package net.microcontroller.backend.service;

import lombok.RequiredArgsConstructor;
import net.microcontroller.backend.dto.CategoryDTO;
import net.microcontroller.backend.model.Category;
import net.microcontroller.backend.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private CategoryRepository categoryRepository;
	private ModelMapper modelMapper;

	@Transactional
	public CategoryDTO createCategory(String name, String description) {
		if (categoryRepository.existsByName(name)) {
			throw new RuntimeException("Category with name " + name + " already exists");
		}

		Category category = Category.builder()
				.name(name)
				.description(description)
				.build();

		Category savedCategory = categoryRepository.save(category);
		return modelMapper.map(savedCategory, CategoryDTO.class);
	}

	public CategoryDTO getCategoryById(UUID categoryId) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
		return modelMapper.map(category, CategoryDTO.class);
	}

	public List<CategoryDTO> getAllCategories() {
		return categoryRepository.findAll().stream()
				.map(category -> modelMapper.map(category, CategoryDTO.class))
				.collect(Collectors.toList());
	}

	public Category findCategoryEntityById(UUID categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
	}

	public Category findCategoryEntityByName(String name) {
		return categoryRepository.findByName(name)
				.orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
	}
}


