package com.shenghesun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shenghesun.dao.UserDao;
import com.shenghesun.entity.User;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	
	public User save(User user) {
		return userDao.save(user);
	}
	
	public User findByOpenId(String openId) {
		return userDao.findByOpenId(openId);
	}
}
