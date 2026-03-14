package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "issue_type")
public class IssueType {

  @Id
  @Column(name = "issue_type_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long issueTypeId;

  @Column(
    name = "issue_type_tag",
    nullable = false,
    unique = true
  )
  private String issueTypeTag;

  @Column(name = "issue_type_description", nullable = false)
  private String issueTypeDescription;

  // used AI to help with ManyToMany annotation
  @ManyToMany(mappedBy = "issueTypes")
  private Set<IssueReport> issueReports = new HashSet<>();
}
