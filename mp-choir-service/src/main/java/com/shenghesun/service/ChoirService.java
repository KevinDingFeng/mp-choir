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
	
	/**
	 * 修改时设置version
	 * @Title: get4Update 
	 * @Description: TODO 
	 * @param choir
	 * @return  Choir 
	 * @author yangzp
	 * @date 2018年8月21日下午2:14:38
	 **/ 
	public Choir get4Update(Choir choir) {
		Optional<Choir> opt = choirDao.findById(choir.getId());
		if(opt!=null) {
			Choir dbChoir = opt.get();
			choir.setVersion(dbChoir.getVersion());
			return choir;
		}
		return null;
	}
	
}
