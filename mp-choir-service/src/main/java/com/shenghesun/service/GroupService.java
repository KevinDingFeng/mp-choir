package com.shenghesun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shenghesun.dao.GroupDao;
import com.shenghesun.entity.Group;

@Service
public class GroupService {

	@Autowired
	private GroupDao groupDao;
	
	public Group save(Group group) {
		return groupDao.save(group);
	}
	
	public Group findByGId(String openGid) {
		return groupDao.findByOpenGid(openGid);
	}
}
