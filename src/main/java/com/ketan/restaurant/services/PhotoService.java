package com.ketan.restaurant.services;

import com.ketan.restaurant.domain.entities.Photo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.ResourceBundle;

public interface PhotoService {

    Photo uploadPhoto(MultipartFile file);
    Optional<Resource> getPhotoAsResource(String id);
}
