package net.microcontroller.backend.repository;

import net.microcontroller.backend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

	@Query("SELECT p FROM Project p WHERE p.user.userId = :userId")
	List<Project> findByUserUserId(UUID userId);

	List<Project> findByCategoryCategoryId(UUID categoryId);
	
	@Query("SELECT p FROM Project p WHERE p.category.name = :categoryName")
	List<Project> findByCategoryName(@Param("categoryName") String categoryName);
	
	@Query("SELECT p FROM Project p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
	List<Project> searchByKeyword(@Param("keyword") String keyword);
	
	@Query("SELECT p FROM Project p WHERE p.category.name = :categoryName AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%)")
	List<Project> searchByCategoryAndKeyword(@Param("categoryName") String categoryName, @Param("keyword") String keyword);
}


