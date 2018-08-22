package com.shenghesun.dao;

import com.shenghesun.entity.BackgroundMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BackgroundMusicDao extends JpaRepository<BackgroundMusic, Long>, JpaSpecificationExecutor<BackgroundMusic> {

    BackgroundMusic findByNameAndPopulationAndSort(String name, int population, int sort);

}
