package com.shenghesun.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shenghesun.entity.Group;

@Repository
public interface GroupDao extends JpaRepository<Group, Long> {

	public Group findByOpenGid(String openGid);
}
