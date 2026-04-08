package edu.cnm.deepdive.seesomethingabq.service.storage;

import edu.cnm.deepdive.seesomethingabq.configuration.StorageConfiguration;
import edu.cnm.deepdive.seesomethingabq.configuration.StorageConfiguration.FilenameProperties;
import edu.cnm.deepdive.seesomethingabq.configuration.StorageConfiguration.FilenameProperties.TimestampProperties;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

/**
 * {@link StorageService} implementation that stores content on the local file system.
 */
@Service
public class LocalFileSystemStorageService implements StorageService {

  private final Path uploadDirectory;
  private final Pattern subdirectoryPattern;
  private final DateFormat formatter;
  private final String filenameFormat;
  private final int randomizerLimit;
  private final List<MediaType> contentTypes;

  private Set<String> whiteList;

  /**
   * Creates a storage service configured by {@link StorageConfiguration}.
   *
   * @param storageConfiguration storage configuration properties.
   * @param applicationHome application home, used when storage is configured relative to the app.
   */
  @Autowired
  public LocalFileSystemStorageService(
      StorageConfiguration storageConfiguration,
      ApplicationHome applicationHome
  ) {
    FilenameProperties filenameProperties = storageConfiguration.getFilename();
    TimestampProperties timestampProperties = filenameProperties.getTimestamp();
    String uploadPath = storageConfiguration.getDirectory();

    uploadDirectory = storageConfiguration.isApplicationHome()
        ? applicationHome.getDir().toPath().resolve(uploadPath)
        : Path.of(uploadPath);

    //noinspection ResultOfMethodCallIgnored
    uploadDirectory.toFile().mkdirs();

    subdirectoryPattern = storageConfiguration.getSubdirectoryPattern();
    whiteList = storageConfiguration.getWhiteList();

    contentTypes = whiteList
        .stream()
        .map(MediaType::valueOf)
        .toList();

    filenameFormat = filenameProperties.getFormat();
    randomizerLimit = filenameProperties.getRandomizerLimit();

    formatter = new SimpleDateFormat(timestampProperties.getFormat());

    formatter.setTimeZone(TimeZone.getTimeZone(timestampProperties.getTimeZone()));
  }

  @Override
  public String store(MultipartFile file) throws IOException, HttpMediaTypeException {
    String contentType = file.getContentType() != null ? file.getContentType() : "";

    if (!whiteList.contains(file.getContentType())) {
      throw new HttpMediaTypeNotSupportedException(contentType, contentTypes);
    }

    String originalFilename = file.getOriginalFilename();
    UUID uuid = UUID.randomUUID();
    ByteBuffer buffer = ByteBuffer.allocate(16);
    buffer.putLong(uuid.getMostSignificantBits());
    buffer.putLong(uuid.getLeastSignificantBits());

    String encoded = Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(buffer.array());

    String newFilename = filenameFormat
        .formatted(
            encoded,
            getExtension(
                (originalFilename != null)
                    ? originalFilename
                    : ""
            )
        );

    String subdirectory = getSubdirectory(newFilename);

    Path resolvedPath = uploadDirectory.resolve(subdirectory);

    //noinspection ResultOfMethodCallIgnored
    resolvedPath.toFile().mkdirs();

    Files.copy(file.getInputStream(), resolvedPath.resolve(newFilename));

    return newFilename;
  }

  @Override
  public Resource retrieve(String key) throws IOException {
    String subdirectory = getSubdirectory(key);

    Path path = uploadDirectory.resolve(subdirectory).resolve(key);

    return new UrlResource(path.toUri());
  }

  @Override
  public boolean delete(String key)
      throws IOException, UnsupportedOperationException, SecurityException {

    String subdirectory = getSubdirectory(key);
    Path path = uploadDirectory.resolve(subdirectory).resolve(key);

    if (Files.exists(path)) {
      Files.delete(path);
      return true;
    } else {
      return false;
    }
  }


  @NonNull
  private String getExtension(@NonNull String filename) {
    int position = filename.lastIndexOf('.');

    return (position >= 0) ? filename.substring(position + 1) : "";
  }

  private String getSubdirectory(@NonNull String filename) {

    Matcher matcher = subdirectoryPattern.matcher(filename);

    return matcher.matches()
        ? IntStream.rangeClosed(1, matcher.groupCount())
        .mapToObj(matcher::group)
        .collect(Collectors.joining("/"))
        : "";
  }

}
