package edu.cnm.deepdive.seesomethingabq.model.dto;

import java.net.URL;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO representing a user profile response enriched with the total number of reports owned
 * by the user.
 *
 * @param externalId server-side external identifier.
 * @param displayName user display name.
 * @param email user email address.
 * @param avatar avatar URL, if set.
 * @param manager whether the user has manager privileges.
 * @param timeCreated timestamp when the profile was created.
 * @param userEnabled whether the user is enabled.
 * @param reportCount total number of issue reports belonging to the user.
 */
public record UserProfileResponse(
    UUID externalId,
    String displayName,
    String email,
    URL avatar,
    boolean manager,
    Instant timeCreated,
    boolean userEnabled,
    long reportCount
) {
}