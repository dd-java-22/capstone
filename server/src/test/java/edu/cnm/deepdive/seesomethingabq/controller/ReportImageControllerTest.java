//package edu.cnm.deepdive.seesomethingabq.controller;
//
//import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
//import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
//import edu.cnm.deepdive.seesomethingabq.service.ReportImageService;
//import java.net.URI;
//import java.util.UUID;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(ReportImageController.class)
//class ReportImageControllerTest {
//
//  @Autowired
//  private MockMvc mvc;
//
//  @MockBean
//  private ReportImageService service;
//
//  @Test
//  void getImage_returnsImage() throws Exception {
//    UUID reportId = UUID.randomUUID();
//    UUID imageId = UUID.randomUUID();
//
//    ReportImage image = new ReportImage();
//    image.setFilename("test.jpg");
//    image.setMimeType("image/jpeg");
//    image.setImageLocator(URI.create("file://test.jpg"));
//
//    Mockito.when(service.getImage(reportId, imageId)).thenReturn(image);
//
//    mvc.perform(get("/issue-reports/" + reportId + "/images/" + imageId))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.filename").value("test.jpg"));
//  }
//
//  @Test
//  void addImage_createsImage() throws Exception {
//    UUID reportId = UUID.randomUUID();
//
//    ReportImage image = new ReportImage();
//    image.setFilename("new.jpg");
//
//    Mockito.when(service.addImage(eq(reportId), any())).thenReturn(image);
//
//    mvc.perform(post("/issue-reports/" + reportId + "/images")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content("""
//                {
//                  "filename": "new.jpg",
//                  "mimeType": "image/jpeg",
//                  "imageLocator": "file://new.jpg",
//                  "albumOrder": 1
//                }
//                """))
//        .andExpect(status().isCreated())
//        .andExpect(jsonPath("$.filename").value("new.jpg"));
//  }
//
//  @Test
//  void deleteImage_returnsNoContent() throws Exception {
//    UUID reportId = UUID.randomUUID();
//    UUID imageId = UUID.randomUUID();
//
//    mvc.perform(delete("/issue-reports/" + reportId + "/images/" + imageId))
//        .andExpect(status().isNoContent());
//
//    Mockito.verify(service).deleteImage(reportId, imageId);
//  }
//}
