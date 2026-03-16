package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(
  name = "issue_type",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_issue_type_issue_type_tag", columnNames = "issue_type_tag")
  }
)
public class IssueType {

  @Id
  @Column(name = "issue_type_id", nullable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String issueTypeTag;

  @Column(nullable = false)
  private String issueTypeDescription;

  // used AI to help with ManyToMany annotation
  @ManyToMany(mappedBy = "issueTypes", fetch = FetchType.LAZY)
  private final List<IssueReport> issueReports = new LinkedList<>();

  public Long getId() {
    return id;
  }

  public String getIssueTypeTag() {
    return issueTypeTag;
  }

  public void setIssueTypeTag(String issueTypeTag) {
    this.issueTypeTag = issueTypeTag;
  }

  public String getIssueTypeDescription() {
    return issueTypeDescription;
  }

  public void setIssueTypeDescription(String issueTypeDescription) {
    this.issueTypeDescription = issueTypeDescription;
  }

  public List<IssueReport> getIssueReports() {
    return issueReports;
  }
}
