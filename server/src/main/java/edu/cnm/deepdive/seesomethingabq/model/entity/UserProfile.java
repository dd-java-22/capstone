package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "user_profile")
public class UserProfile {

  @Id
  @Column(name = "user_profile_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userProfileId;

  @Column(
    name = "oauth_key",
    nullable = false,
    unique = true
  )
  private String oauthKey;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "is_manager", nullable = false)
  private Boolean isManager;

  @CreationTimestamp
  @Column(
    name = "time_created",
    nullable = false,
    updatable = false
  )
  private Instant timeCreated;

  @Column(name = "user_enabled", nullable = false)
  private Boolean userEnabled;
}
