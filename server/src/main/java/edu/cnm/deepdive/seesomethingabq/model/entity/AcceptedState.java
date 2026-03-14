package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "accepted_state")
public class AcceptedState {

  @Id
  @Column(name = "accepted_state_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long acceptedStateId;

  @Column(
    name = "status_tag",
    nullable = false,
    unique = true
  )
  private String statusTag;

  @Column(name = "status_tag_description", nullable = false)
  private String statusTagDescription;
}
