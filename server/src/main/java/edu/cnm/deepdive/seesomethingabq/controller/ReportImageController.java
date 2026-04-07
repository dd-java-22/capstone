package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.service.ReportImageService;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller for managing report images. Provides endpoints for retrieving and adding images
 * to issue reports. Access control is enforced at the service layer.
 */
@RestController
@RequestMapping("/issue-reports/{externalId}/images")
public class ReportImageController {

  private final ReportImageService service;

  /**
   * Constructs an instance of {@code ReportImageController} with the specified service.
   *
   * @param service Report image service for business logic operations.
   */
  @Autowired
  public ReportImageController(ReportImageService service) {
    this.service = service;
  }

  /**
   * Retrieves a specific image from an issue report. Access is restricted to the report owner and
   * managers.
   *
   * @param externalId The external ID (UUID) of the issue report.
   * @param imageId The external ID (UUID) of the image.
   * @return The requested report image.
   */
  @GetMapping(value = "/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ReportImage getImage(@PathVariable UUID externalId, @PathVariable UUID imageId) {
    return service.getImage(externalId, imageId);
  }

  /**
   * Adds a new image to an issue report. Only the report owner can add images.
   *
   * @param externalId The external ID (UUID) of the issue report.
   * @param request The image data to add.
   * @return The newly created report image.
   */
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReportImage> addImage(@PathVariable UUID externalId,
      @RequestBody AddImageRequest request) {
    ReportImage created = service.addImage(externalId, request);
    URI location = linkTo(methodOn(ReportImageController.class)
        .getImage(externalId, created.getExternalId()))
        .toUri();
    return ResponseEntity.created(location).body(created);
  }

  /**
   * Deletes an Image from an existing issue report. Only the report owner can delete images.
   *
   * @param externalId The external ID (UUID) of the issue report.
   */
  @DeleteMapping("/{imageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteImage(@PathVariable UUID externalId, @PathVariable UUID imageId) {
    service.deleteImage(externalId, imageId);
  }
}
