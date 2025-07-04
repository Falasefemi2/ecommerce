package com.femmie.ecommerce.controller;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.http.HttpHeaders;

import com.femmie.ecommerce.dto.ImageDto;
import com.femmie.ecommerce.exception.ResourceNotFoundException;
import com.femmie.ecommerce.model.Image;
import com.femmie.ecommerce.service.image.ImageService;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @Test
    void testDeleteImage() throws Exception {
        Long imageId = 1L;

        doNothing().when(imageService).deleteImageById(imageId);

        mockMvc.perform(delete("/api/v1/images/{imageId}", imageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Delete success"));

    }

    @Test
    void testDownloadImage() throws Exception {
        Long imageId = 1L;
        byte[] fakeImageBytes = "fake-image-bytes".getBytes();

        Image image = new Image();
        image.setId(imageId);
        image.setFileName("sample.jpg");
        image.setFileType("image/jpeg");
        image.setImage(fakeImageBytes);

        when(imageService.getImageById(imageId)).thenReturn(image);

        mockMvc.perform(get("/api/v1/images/download/{imageId}", imageId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sample.jpg\""))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/jpeg"))
                .andExpect(content().bytes(fakeImageBytes));
    }

    @Test
    void testDownloadImage_NotFound() throws Exception {
        Long imageId = 999L;
        when(imageService.getImageById(imageId)).thenThrow(new ResourceNotFoundException("Image not found"));

        mockMvc.perform(get("/api/v1/images/download/{imageId}", imageId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSaveImages() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpg", "image/jpeg", "dummy-content-1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "image2.jpg", "image/jpeg", "dummy-content-2".getBytes());
        Long productId = 1L;

        ImageDto dto1 = new ImageDto(1L, "image1.jpg", "url1.jpg");
        ImageDto dto2 = new ImageDto(2L, "image2.jpg", "url2.jpg");

        when(imageService.saveImages(anyList(), eq(productId))).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(multipart("/api/v1/images")
                .file(file1)
                .file(file2)
                .param("productId", String.valueOf(productId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Upload success"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].fileName").value("image1.jpg"))
                .andExpect(jsonPath("$.data[1].fileName").value("image2.jpg"));
    }

    @Test
    void testSaveImages_InternalServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "image.jpg", "image/jpeg", "dummy".getBytes());

        when(imageService.saveImages(anyList(), eq(1L)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(multipart("/api/v1/images")
                .file(file)
                .param("productId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to upload image(s): Database error"));
    }

    @Test
    void testSaveImages_NoProductId() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "image.jpg", "image/jpeg", "dummy".getBytes());

        mockMvc.perform(multipart("/api/v1/images")
                .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveImages_NoFiles() throws Exception {
        mockMvc.perform(multipart("/api/v1/images")
                .param("productId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateImage() throws Exception {
        Long imageId = 1L;

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "updated-image.jpg",
                "image/jpeg",
                "UpdatedImageContent".getBytes()
        );

        doNothing().when(imageService).updateImage(any(MultipartFile.class), eq(imageId));

        mockMvc.perform(multipart("/api/v1/images/{imageId}/update", imageId)
                .file(mockFile)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update success!"));
    }

    @Test
    void testUpdateImage_ImageNotFound() throws Exception {
        Long imageId = 100L;

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "new.jpg", "image/jpeg", "data".getBytes());

        doThrow(new ResourceNotFoundException("Image not found")).when(imageService).updateImage(any(), eq(imageId));

        mockMvc.perform(multipart("/api/v1/images/{imageId}/update", imageId)
                .file(mockFile)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Image not found: Image not found"));
    }

}
