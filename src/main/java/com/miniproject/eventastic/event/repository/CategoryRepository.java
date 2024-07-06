package com.miniproject.eventastic.event.repository;

import com.miniproject.eventastic.event.metadata.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
