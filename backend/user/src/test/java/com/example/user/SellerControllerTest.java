package com.example.user;


import com.example.user.controller.SellerController;
import com.example.user.data.entity.Restaurant;
import com.example.user.data.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SellerControllerTest {

    private final SellerController sellerController;
    private final MockMvc mockMvc;

    private final String sessionId;

    public SellerControllerTest() {
        S3Client s3Client = Mockito.mock(S3Client.class);
        RestaurantRepository restaurantRepositoryMock = Mockito.mock(RestaurantRepository.class);
        this.sellerController = new SellerController(s3Client, restaurantRepositoryMock);
        this.mockMvc = MockMvcBuilders.standaloneSetup(sellerController).build();

        this.sessionId = "test_session_id";

        // MyService가 processData 메서드를 호출할 때 "Mock data"를 반환하도록 설정
        when(restaurantRepositoryMock.save(any())).thenReturn(sessionId);
//        when(restaurantRepositoryMock.findAll()).thenReturn(sessionId)
//        when(restaurantRepositoryMock.findById())


    }


    @Test
    void uploadRestaurantPicture_WithValidFile_ShouldReturnOk() throws Exception {
        // Mock MultipartFile
        MockMultipartFile file = new MockMultipartFile("file", "test.png",
                MediaType.IMAGE_PNG_VALUE, "image data".getBytes());


        // Perform request
        mockMvc.perform(multipart("/restaurant-picture")
                .file(file)
                .param("sessionId", sessionId))
                .andExpect(status().isOk());

        // Verify that S3Client.putObject is called with the correct parameters
//        Mockito.verify(sellerController.getS3Client()).putObject(any(), any(), any());
    }

    @Test
    void uploadRestaurantPicture_WithInvalidFileType_ShouldReturnBadRequest() throws Exception {
        // Mock MultipartFile
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "image data".getBytes());
        String sessionId = "test_session_id";

        // Perform request and expect BadRequest (400)
        mockMvc.perform(multipart("/restaurant-picture")
                .file(file)
                .param("sessionId", sessionId))
                .andExpect(status().isBadRequest());
    }
}
