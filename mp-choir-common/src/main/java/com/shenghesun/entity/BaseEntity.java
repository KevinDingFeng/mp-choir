package com.shenghesun.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shenghesun.ai.entity.listener.BaseEntityListener;

@MappedSuperclass
@EntityListeners(value = {BaseEntityListener.class})
public class BaseEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2166750384646114468L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;
	
	@Version
	protected Long version = 0l;
	
	@JsonFormat(timezone="GMT+8")
	@Column(updatable=false, nullable=false)
	protected Timestamp creation = new Timestamp(System.currentTimeMillis());
	
//	@Index(name="lastModified")
	@JsonFormat(timezone="GMT+8")
	@Column(nullable=false)
	protected Timestamp lastModified = new Timestamp(System.currentTimeMillis());

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Timestamp getCreation() {
		return creation;
	}

	public void setCreation(Timestamp creation) {
		this.creation = creation;
	}

	public Timestamp getLastModified() {
		return lastModified;
	}

	public void setLastModified(Timestamp lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
