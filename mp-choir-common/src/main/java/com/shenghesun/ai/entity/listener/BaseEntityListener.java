package com.shenghesun.ai.entity.listener;

import java.sql.Timestamp;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.shenghesun.entity.BaseEntity;

public class BaseEntityListener {

	public BaseEntityListener() {
		
	}
	
	@PrePersist
	public void onPrePersist(BaseEntity e) {
		long times = System.currentTimeMillis();
		Timestamp t = new Timestamp(times);
		e.setCreation(t);
		e.setLastModified(t);
	}
	
	@PreUpdate
	public void onPreUpdate(BaseEntity e) {
		long times = System.currentTimeMillis();
		Timestamp t = new Timestamp(times);
		e.setLastModified(t);
	}
	
}
