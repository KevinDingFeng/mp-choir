package com.shenghesun.dao;

import com.shenghesun.entity.SyntheticSongs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyntheticSongsDao extends JpaRepository<SyntheticSongs, Long>, JpaSpecificationExecutor<SyntheticSongs> {

    List<SyntheticSongs> findByUserIdsLike(String userId);

    SyntheticSongs findByChoirId(Long choirId);

}
