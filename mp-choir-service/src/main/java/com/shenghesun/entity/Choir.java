package com.shenghesun.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

//合唱团信息
@Entity
@Table
@Data
@ToString(exclude = {"users"})
@EqualsAndHashCode(exclude = {"users"},callSuper = false)
@JsonIgnoreProperties("users")
public class Choir extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 5855327768914111196L;

	/**
	 * 团名称
	 */
	@Column(nullable = false, length = 10)
	private String choirName;
	/**
	 * 合唱人数
	 */
	@Column
	private int population;
	
	
	/**
	 * 专辑封面路径
	 */
	@Column(nullable = false, length = 100)
	private String albumArtPaht;
	
	/**
	 * 状态 0：未合成；1：已合成
	 */
	@Column
	private int status=0;
	
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "choir_user_rel", 
	inverseJoinColumns = {@JoinColumn(name = "user_id")}, 
	joinColumns = {@JoinColumn(name = "choir_id")})
	private Set<User> users;
	
	
}
