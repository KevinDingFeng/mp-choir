package com.shenghesun.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
  * 合成后的歌曲
  * @ClassName: SyntheticSongs 
  * @Description: TODO
  * @author: yangzp
  * @date: 2018年8月13日 上午11:42:07  
  */
@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = false)
public class SyntheticSongs extends BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8895439557839338931L;
	
	/**
	 * 合唱团id
	 */
	@Column
	private Long choirId;
	
	/**
	 * 合唱成员id，逗号分隔
	 */
	@Column
	private String userIds;
	
	/**
	 * 合成后歌曲路径
	 */
	@Column(nullable = false, length = 100)
	private String songPath;

	@Column
	private String wxacodePath;
}
