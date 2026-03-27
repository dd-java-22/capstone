package edu.cnm.deepdive.seesomethingabq.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.net.URI;

/**
 * Persists {@link URI} values as {@link String} (VARCHAR) columns instead of relying on default
 * Java-serialization-based mapping (which commonly results in VARBINARY/BLOB types).
 */
@Converter
public class UriAttributeConverter implements AttributeConverter<URI, String> {

  @Override
  public String convertToDatabaseColumn(URI attribute) {
    return (attribute != null) ? attribute.toString() : null;
  }

  @Override
  public URI convertToEntityAttribute(String dbData) {
    return (dbData != null && !dbData.isBlank()) ? URI.create(dbData) : null;
  }

}

