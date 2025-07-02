package com.femmie.ecommerce.controller;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.femmie.ecommerce.dto.ImageDto;
import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Image;
import com.femmie.ecommerce.response.ApiResponse;
import com.femmie.ecommerce.service.image.IImageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private final IImageService imageService;

    @PostMapping
    public ResponseEntity<ApiResponse> saveImages(
            @RequestParam List<MultipartFile> files,
            @RequestParam Long productId) {

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("No files uploaded", null));
        }

        if (productId == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Product ID is required", null));
        }

        try {
            List<ImageDto> imageDto = imageService.saveImages(files, productId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Upload success", imageDto));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Failed to upload image(s): " + e.getMessage(), null));
        }
    }

    @GetMapping("/download/{imageId}")
    public ResponseEntity<ByteArrayResource> downloadImage(@PathVariable Long imageId) {
        Image image = imageService.getImageById(imageId);
        ByteArrayResource resource = new ByteArrayResource(image.getImage());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/{imageId}/update")
    public ResponseEntity<ApiResponse> updateImage(
            @PathVariable Long imageId,
            @RequestParam MultipartFile file) {

        try {
            imageService.updateImage(file, imageId);
            return ResponseEntity.ok(new ApiResponse("Update success!", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Image not found: " + e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Update failed: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) {
        try {
            imageService.deleteImageById(imageId);
            return ResponseEntity.ok(new ApiResponse("Delete success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Image not found: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Delete failed: " + e.getMessage(), null));
        }
    }
}
