package com.justdevthat.entity;

import javax.persistence.*;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode()
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long telegramUserId;

  @CreationTimestamp
  private LocalDateTime firstLoginDate;

  private String firstName;
  private String lastName;
  private String userName;
  private String email;
  private Boolean isActive;
  @Enumerated(EnumType.STRING)
  private UserState userState;
}
