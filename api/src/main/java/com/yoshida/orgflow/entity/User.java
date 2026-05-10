package com.yoshida.orgflow.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  public User() {
  }

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "display_name", nullable = false, length = 100)
  private String displayName;

  @Column(name = "login_id", nullable = false, length = 100)
  private String loginId;

  @Column(name = "hashed_password", nullable = false)
  private String hashedPassword;

  @Column(name = "mail_address", nullable = false)
  private String mailAddress;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public UUID getId() {
    return id;
  }

  public String getLoginId() {
    return loginId;
  }

  public String getHashedPassword() {
    return hashedPassword;
  }
}
