package com.shenghesun.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

 /**
  * 背景音乐信息
  * @ClassName: BackgroundMusic 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月20日 上午11:49:19  
  */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = true)
public class BackgroundMusic extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7805180837808790474L;
	
	/**
	 * 音乐名称
	 */
	@Column(nullable = false, length = 100)
	private String name;
	
	/**
	 * 合唱人数即为分段数
	 */
	@Column(nullable = false)
	private int population;
	
	/**
	 * 背景音乐链接
	 */
	@Column(nullable = false, length = 100)
	private String musicPath;
	
	/**
	 * 音乐歌词
	 */
	@Column(nullable = false, length = 500)
	private String lyric;
	
	/**
	 * 单曲播放时长
	 */
	@Column
	private int uration;
	
	/**
	 * 分段音乐顺序
	 */
	@Column(nullable = false)
	private int sort;
}
