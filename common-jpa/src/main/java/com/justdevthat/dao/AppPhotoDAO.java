package com.justdevthat.dao;

import com.justdevthat.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
