package com.miniproject.eventastic.image.repository;

import com.miniproject.eventastic.image.entity.ImageEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageEventRepository extends JpaRepository<ImageEvent, Long> {

}
