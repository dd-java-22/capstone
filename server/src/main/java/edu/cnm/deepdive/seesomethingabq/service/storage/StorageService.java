package edu.cnm.deepdive.seesomethingabq.service.storage;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for storing, retrieving, and deleting binary content by key.
 */
public interface StorageService {

  /**
   * Stores the provided file and returns a storage key.
   *
   * @param file file to store.
   * @return storage key.
   * @throws IOException if storage fails due to an I/O error.
   * @throws HttpMediaTypeException if the file media type is not supported.
   */
  String store(MultipartFile file) throws IOException, HttpMediaTypeException;

  /**
   * Retrieves the stored content for the specified key.
   *
   * @param key storage key.
   * @return resource representing the stored content.
   * @throws IOException if retrieval fails due to an I/O error.
   */
  Resource retrieve(String key) throws IOException;

  /**
   * Deletes stored content for the specified key.
   *
   * @param key storage key.
   * @return {@code true} if a resource existed and was deleted; {@code false} otherwise.
   * @throws IOException if deletion fails due to an I/O error.
   * @throws UnsupportedOperationException if deletion is not supported by the implementation.
   * @throws SecurityException if deletion is denied by the underlying platform/security manager.
   */
  boolean delete(String key)
      throws IOException, UnsupportedOperationException, SecurityException;

  /**
   * Container for content retrieved from storage.
   *
   * @param filename logical or original filename.
   * @param contentType content type.
   * @param resource resource containing the content.
   */
  record StorageReference(String filename, String contentType, Resource resource) {
  }

}
