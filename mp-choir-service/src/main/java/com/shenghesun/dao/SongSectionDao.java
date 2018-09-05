package com.shenghesun.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shenghesun.entity.SongSection;
import com.shenghesun.entity.SongSection.SectionStatusEnum;

@Repository
public interface SongSectionDao extends JpaRepository<SongSection, Long>, JpaSpecificationExecutor<SongSection> {
	
	public List<SongSection> findByChoirIdOrderBySortAsc(Long id);
	
	public List<SongSection> findByUserIdAndStatus(Long userId, SectionStatusEnum status);
}
