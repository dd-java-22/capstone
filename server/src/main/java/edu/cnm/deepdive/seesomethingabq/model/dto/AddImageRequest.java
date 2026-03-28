package edu.cnm.deepdive.seesomethingabq.model.dto;

import java.net.URI;

/**
 * Request body DTO for adding an image to an issue report.
 */
public class AddImageRequest {

  private URI imageLocator;
  private String filename;
  private String mimeType;
  private int albumOrder;

  public URI getImageLocator() {
    return imageLocator;
  }

  public void setImageLocator(URI imageLocator) {
    this.imageLocator = imageLocator;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public int getAlbumOrder() {
    return albumOrder;
  }

  public void setAlbumOrder(int albumOrder) {
    this.albumOrder = albumOrder;
  }
}
