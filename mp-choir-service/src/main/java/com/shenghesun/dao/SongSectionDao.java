package com.shenghesun.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shenghesun.entity.SongSection;
import com.shenghesun.entity.SongSection.SectionStatusEnum;

@Repository
public interface SongSectionDao extends JpaRepository<SongSection, Long>, JpaSpecificationExecutor<SongSection> {
	
	public List<SongSection> findByChoirIdOrderBySortAsc(Long id);
	
	public List<SongSection> findByUserIdAndStatusAndPastTimeGreaterThan(Long userId, SectionStatusEnum status, Timestamp pastTime);
	
	@Query("SELECT ss FROM SongSection ss where ss.userId = ?1 and (ss.status = 'NO_RECORDING' or ss.status = 'RECORDED') and ss.choir.status=0 and ss.pastTime>?2")
	public List<SongSection> findMySection(Long userId, Timestamp pastTime);
	
}
