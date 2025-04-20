package com.justdevthat.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_document")
public class AppDocument {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String telegramFileId;
  private String docName;
  @OneToOne
  private BinaryContent binaryContent;

  private String mimeType;
  private Long fileSize;
}
