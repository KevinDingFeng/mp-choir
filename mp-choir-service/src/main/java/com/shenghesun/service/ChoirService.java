package com.shenghesun.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shenghesun.dao.ChoirDao;
import com.shenghesun.entity.Choir;

@Service
@Transactional(readOnly=true)
public class ChoirService {

	@Autowired
	private ChoirDao choirDao;
	@Transactional(readOnly=false)
	public Choir save(Choir choir) {
		return choirDao.save(choir);
	}
	
	public Choir getForUpdate(Long id) {
		Optional<Choir> opt = choirDao.findById(id);
		if(opt!=null) {
			return opt.get();
		}
		return null;
	}
	
}
