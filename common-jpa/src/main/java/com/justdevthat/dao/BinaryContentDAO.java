package com.justdevthat.dao;

import com.justdevthat.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
