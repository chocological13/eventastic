package com.miniproject.eventastic.image.repository;

import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageUserAvatarRepository extends JpaRepository<ImageUserAvatar, Long> {

}
