package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.service.ReportImageService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing report images. Provides endpoints for retrieving and adding images
 * to issue reports. Access control is enforced at the service layer.
 */
@RestController
@RequestMapping("/issue-reports/{externalKey}/images")
public class ReportImageController {

  private final ReportImageService service;

  @Autowired
  public ReportImageController(ReportImageService service) {
    this.service = service;
  }

  @GetMapping(value = "/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ReportImage getImage(@PathVariable UUID externalKey, @PathVariable UUID imageId) {
    return service.getImage(externalKey, imageId);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ReportImage addImage(@PathVariable UUID externalKey,
      @RequestBody AddImageRequest request) {
    return service.addImage(externalKey, request);
  }

  @DeleteMapping("/{imageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteImage(@PathVariable UUID externalKey, @PathVariable UUID imageId) {
    service.deleteImage(externalKey, imageId);
  }
}
