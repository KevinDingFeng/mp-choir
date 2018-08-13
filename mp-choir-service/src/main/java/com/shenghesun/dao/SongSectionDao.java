package com.shenghesun.dao;

import com.shenghesun.entity.SongSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SongSectionDao extends JpaRepository<SongSection, Long>, JpaSpecificationExecutor<SongSection> {
}
