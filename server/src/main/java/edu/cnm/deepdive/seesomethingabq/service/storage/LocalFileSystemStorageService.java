package edu.cnm.deepdive.seesomethingabq.service.storage;

import edu.cnm.deepdive.seesomethingabq.configuration.StorageConfiguration;
import edu.cnm.deepdive.seesomethingabq.configuration.StorageConfiguration.FilenameProperties.TimestampProperties;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileSystemStorageService implements StorageService {

  private final Path uploadDirectory;
  private final Pattern subdirectoryPattern;
  private final List<MediaType> supportedMediaTypes;
  private final String unknownFilename;
  private final String filenameFormat;
  private final int randomizerLimit;
  private final DateTimeFormatter timestampFormatter;

  public LocalFileSystemStorageService(StorageConfiguration storageConfiguration,
      ApplicationHome applicationHome) throws IOException {
    StorageConfiguration.FilenameProperties filenameProperties = storageConfiguration.getFilename();
    TimestampProperties timestampProperties =
        (filenameProperties != null) ? filenameProperties.getTimestamp() : null;

    String uploadPath = storageConfiguration.getPath();
    uploadPath = (uploadPath == null || uploadPath.isBlank()) ? storageConfiguration.getDirectory() : uploadPath;
    if (storageConfiguration.isApplicationHome()) {
      Path base = applicationHome.getDir().toPath();
      uploadDirectory = base.resolve(uploadPath).normalize();
    } else {
      uploadDirectory = Path.of(uploadPath).toAbsolutePath().normalize();
    }
    Files.createDirectories(uploadDirectory);

    subdirectoryPattern = storageConfiguration.getSubdirectoryPattern();
    Set<String> whiteList = storageConfiguration.getWhiteList();
    supportedMediaTypes = whiteList.stream().map(MediaType::parseMediaType).toList();

    unknownFilename = (filenameProperties != null) ? filenameProperties.getUnknown() : null;
    filenameFormat = (filenameProperties != null) ? filenameProperties.getFormat() : null;
    randomizerLimit = (filenameProperties != null) ? filenameProperties.getRandomizerLimit() : 0;
    timestampFormatter = (timestampProperties != null
        && timestampProperties.getFormat() != null && !timestampProperties.getFormat().isBlank()
        && timestampProperties.getTimeZone() != null && !timestampProperties.getTimeZone().isBlank())
        ? DateTimeFormatter.ofPattern(timestampProperties.getFormat())
        .withZone(ZoneId.of(timestampProperties.getTimeZone()))
        : null;
  }

  public LocalFileSystemStorageService(StorageConfiguration storageConfiguration) throws IOException {
    this(storageConfiguration, new ApplicationHome(LocalFileSystemStorageService.class));
  }

  @Override
  public String store(MultipartFile file) throws IOException, HttpMediaTypeException {
    MediaType requestType = null;
    String contentType = file.getContentType();
    if (contentType != null && !contentType.isBlank()) {
      requestType = MediaType.parseMediaType(contentType);
    }
    if (requestType == null || !isAllowed(requestType)) {
      throw new HttpMediaTypeNotSupportedException(requestType, supportedMediaTypes);
    }

    UUID uuid = UUID.randomUUID();
    byte[] bytes = new byte[16];
    long msb = uuid.getMostSignificantBits();
    long lsb = uuid.getLeastSignificantBits();
    for (int i = 0; i < 8; i++) {
      bytes[i] = (byte) (msb >>> (56 - (i * 8)));
      bytes[i + 8] = (byte) (lsb >>> (56 - (i * 8)));
    }
    String keyBase = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null || originalFilename.isBlank()) {
      originalFilename = unknownFilename;
    }
    String extension = getExtension(originalFilename);

    String key = formatFilename(keyBase, extension);
    Path subdirectory = getSubdirectory(key);
    Path target = uploadDirectory.resolve(subdirectory);
    Files.createDirectories(target);
    target = target.resolve(key);
    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    return key;
  }

  @Override
  public Resource retrieve(String key) throws IOException {
    Path path = uploadDirectory.resolve(getSubdirectory(key)).resolve(key);
    try {
      return new UrlResource(path.toUri());
    } catch (MalformedURLException e) {
      throw new IOException(e);
    }
  }

  @Override
  public boolean delete(String key) throws IOException, UnsupportedOperationException, SecurityException {
    return false;
  }

  private boolean isAllowed(MediaType requestType) {
    return supportedMediaTypes.stream()
        .anyMatch((allowed) -> allowed.includes(requestType) || allowed.isCompatibleWith(requestType));
  }

  private String formatFilename(String base, String extension) {
    String timestamp = (timestampFormatter != null)
        ? timestampFormatter.format(ZonedDateTime.now(timestampFormatter.getZone()))
        : "";
    int randomizer = (randomizerLimit > 0) ? ThreadLocalRandom.current().nextInt(randomizerLimit) : 0;
    if (filenameFormat == null || filenameFormat.isBlank()) {
      return base + extension;
    }
    try {
      return String.format(filenameFormat, base, extension, timestamp, randomizer);
    } catch (IllegalFormatException ex) {
      return base + extension;
    }
  }

  private String getExtension(String filename) {
    if (filename == null) {
      return "";
    }
    int lastSlash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
    int dot = filename.lastIndexOf('.');
    if (dot <= lastSlash) {
      return "";
    }
    return filename.substring(dot);
  }

  private Path getSubdirectory(String filename) {
    if (filename == null) {
      return Path.of("");
    }
    Matcher matcher = subdirectoryPattern.matcher(filename);
    if (!matcher.matches()) {
      return Path.of("");
    }
    return Path.of(matcher.group(1), matcher.group(2), matcher.group(3));
  }

}
