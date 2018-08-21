package com.shenghesun.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

 /**
  * 歌曲分段时长
  * @ClassName: SplitSongDuration 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月21日 下午2:27:27  
  */
@Entity
@Table
@EqualsAndHashCode(callSuper = true)
@Data
public class SplitSongDuration extends BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2707334924205826438L;
	
	/**
	 * 歌名
	 */
	@Column(length = 100)
	private String songName;
	
	/**
	 * 分割时间点
	 */
	@Column(length = 200)
	private String splitTimes;
	
	/**
	 * 合唱团人数，分段标志2-6段
	 */
	@Column
	private int population;
}
