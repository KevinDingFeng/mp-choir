package com.shenghesun.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shenghesun.entity.Choir;

@Repository
public interface ChoirDao extends JpaRepository<Choir, Long> {

}
