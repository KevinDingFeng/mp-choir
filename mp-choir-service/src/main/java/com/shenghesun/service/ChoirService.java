package com.shenghesun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shenghesun.dao.ChoirDao;
import com.shenghesun.entity.Choir;

@Service
public class ChoirService {

	@Autowired
	private ChoirDao choirDao;
	
	public Choir save(Choir choir) {
		return choirDao.save(choir);
	}
	
}
