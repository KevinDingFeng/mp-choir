package com.shenghesun.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.shenghesun.entity.SplitSongDuration;

 /**
  * 歌曲分段时长
  * @ClassName: SplitSongDurationDao 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月21日 下午3:58:14  
  */
@Repository
public interface SplitSongDurationDao extends JpaRepository<SplitSongDuration, Long>, JpaSpecificationExecutor<SplitSongDuration> {
	
	public List<SplitSongDuration> findBySongNameAndPopulation(String songName, int population);
	
}
