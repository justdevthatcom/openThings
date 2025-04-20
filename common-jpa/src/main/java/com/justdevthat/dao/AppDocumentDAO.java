package com.justdevthat.dao;

import com.justdevthat.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
