package com.femmie.ecommerce.service.image;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.femmie.ecommerce.dto.ImageDto;
import com.femmie.ecommerce.model.Image;

public interface IImageService {
    Image getImageById(Long id);

    void deleteImageById(Long id);

    List<ImageDto> saveImages(List<MultipartFile> files, Long productId);

    void updateImage(MultipartFile file, Long imageId);
}
