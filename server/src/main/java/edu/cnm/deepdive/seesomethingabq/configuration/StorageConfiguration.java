package edu.cnm.deepdive.seesomethingabq.configuration;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for server-side file storage.
 *
 * <p>Values are bound from properties with the {@code storage} prefix.</p>
 */
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageConfiguration {

  private boolean applicationHome;
  private String path;
  private List<String> contentTypes;
  private FilenameProperties filename;
  private String directory = "uploads";
  private Pattern subdirectoryPattern = Pattern.compile("^(.{2})(.{2})(.{2}).*$");
  private Set<String> whiteList = new LinkedHashSet<>();

  /**
   * Returns whether storage paths are resolved relative to the application home.
   *
   * @return {@code true} if application home is used; {@code false} otherwise.
   */
  public boolean isApplicationHome() {
    return applicationHome;
  }

  /**
   * Sets whether storage paths are resolved relative to the application home.
   *
   * @param applicationHome {@code true} to use application home; {@code false} otherwise.
   */
  public void setApplicationHome(boolean applicationHome) {
    this.applicationHome = applicationHome;
  }

  /**
   * Returns the configured root storage path, if any.
   *
   * @return storage path.
   */
  public String getPath() {
    return path;
  }

  /**
   * Sets the configured root storage path.
   *
   * @param path storage path.
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Returns the allowed/expected content types for uploaded content.
   *
   * @return content types.
   */
  public List<String> getContentTypes() {
    return contentTypes;
  }

  /**
   * Sets the allowed/expected content types for uploaded content.
   *
   * @param contentTypes content types.
   */
  public void setContentTypes(List<String> contentTypes) {
    this.contentTypes = contentTypes;
  }

  /**
   * Returns filename-related configuration.
   *
   * @return filename properties.
   */
  public FilenameProperties getFilename() {
    return filename;
  }

  /**
   * Sets filename-related configuration.
   *
   * @param filename filename properties.
   */
  public void setFilename(FilenameProperties filename) {
    this.filename = filename;
  }

  /**
   * Returns the directory name used under the storage root.
   *
   * @return directory name.
   */
  public String getDirectory() {
    return directory;
  }

  /**
   * Sets the directory name used under the storage root.
   *
   * @param directory directory name.
   */
  public void setDirectory(String directory) {
    this.directory = directory;
  }

  /**
   * Returns the regex used to compute storage subdirectories.
   *
   * @return subdirectory pattern.
   */
  public Pattern getSubdirectoryPattern() {
    return subdirectoryPattern;
  }

  /**
   * Sets the regex used to compute storage subdirectories.
   *
   * @param subdirectoryPattern subdirectory pattern.
   */
  public void setSubdirectoryPattern(Pattern subdirectoryPattern) {
    this.subdirectoryPattern = subdirectoryPattern;
  }

  /**
   * Returns the configured whitelist of allowed content types/extensions.
   *
   * @return whitelist set.
   */
  public Set<String> getWhiteList() {
    return whiteList;
  }

  /**
   * Sets the configured whitelist of allowed content types/extensions.
   *
   * @param whiteList whitelist set.
   */
  public void setWhiteList(Set<String> whiteList) {
    this.whiteList = whiteList;
  }

  /**
   * Nested configuration group for filename generation/parsing.
   */
  public static class FilenameProperties {

    private String unknown;
    private String format;
    private int randomizerLimit;
    private TimestampProperties timestamp;

    /**
     * Returns the placeholder filename used when the original name is not available.
     *
     * @return unknown filename placeholder.
     */
    public String getUnknown() {
      return unknown;
    }

    /**
     * Sets the placeholder filename used when the original name is not available.
     *
     * @param unknown unknown filename placeholder.
     */
    public void setUnknown(String unknown) {
      this.unknown = unknown;
    }

    /**
     * Returns the filename format pattern.
     *
     * @return filename format.
     */
    public String getFormat() {
      return format;
    }

    /**
     * Sets the filename format pattern.
     *
     * @param format filename format.
     */
    public void setFormat(String format) {
      this.format = format;
    }

    /**
     * Returns the upper bound used for randomizer value generation.
     *
     * @return randomizer limit.
     */
    public int getRandomizerLimit() {
      return randomizerLimit;
    }

    /**
     * Sets the upper bound used for randomizer value generation.
     *
     * @param randomizerLimit randomizer limit.
     */
    public void setRandomizerLimit(int randomizerLimit) {
      this.randomizerLimit = randomizerLimit;
    }

    /**
     * Returns timestamp-related filename configuration.
     *
     * @return timestamp properties.
     */
    public TimestampProperties getTimestamp() {
      return timestamp;
    }

    /**
     * Sets timestamp-related filename configuration.
     *
     * @param timestamp timestamp properties.
     */
    public void setTimestamp(TimestampProperties timestamp) {
      this.timestamp = timestamp;
    }

    /**
     * Nested configuration group for timestamp formatting.
     */
    public static class TimestampProperties {

      private String format;
      private String timeZone;

      /**
       * Returns the timestamp format pattern.
       *
       * @return timestamp format.
       */
      public String getFormat() {
        return format;
      }

      /**
       * Sets the timestamp format pattern.
       *
       * @param format timestamp format.
       */
      public void setFormat(String format) {
        this.format = format;
      }

      /**
       * Returns the time zone identifier used for timestamp formatting.
       *
       * @return time zone ID.
       */
      public String getTimeZone() {
        return timeZone;
      }

      /**
       * Sets the time zone identifier used for timestamp formatting.
       *
       * @param timeZone time zone ID.
       */
      public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
      }

    }

  }

}
