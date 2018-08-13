package com.shenghesun.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
  * 歌曲分段
  * @ClassName: SongSection 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月13日 上午11:34:02  
  */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = false)
public class SongSection extends BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7496767747592566932L;
	
	/**
	 * 合唱团id
	 */
	@Column
	private Long choirId;
	
	/**
	 * 用户id
	 */
	@Column
	private Long userId;
	
	/**
	 * /切割后的资源 id，通过此 id 使用短音频资源
	 */
	@Column
	private String resourceId;
	

}
