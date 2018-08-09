package com.shenghesun.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

//微信用户
@Entity
@Table(name = "wx_user")
@Data
@ToString(exclude = {"groups"})
@EqualsAndHashCode(exclude = {"groups"})
@JsonIgnoreProperties("groups")
public class User implements Serializable {

	private static final long serialVersionUID = -7876850738135969925L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String openId;
	@Column
	private String nickName;
	@Column
	private String gender;
	@Column
	private String language;
	@Column
	private String city;
	@Column
	private String province;
	@Column
	private String country;
	@Column
	private String avatarUrl;
	@Column
	private String unionId;
	
	//测试页面选择的性别 0: 男女1: 男
	@Column
	private int mpGender; 
	//测试页面选择的星座 0-11 分别代表12星座[白羊座,金牛座,双子座,巨蟹座,狮子座 ,处女座,天秤座 ,天蝎座 ,射手座  ,摩羯座 ,水瓶座 ,双鱼座]
	@Column
	private int constellation;
	@Column
	private int age;
	
	//分享群次数 分享5次解锁查看群成员习题答案权限
	@Column
	private int shareCount;
	
	//答题总分
	@Column
	private int totalPoints;
	
	//每道题的答题记录 1,2,3...
	@Column
	private String record;
	
	//其他用户和我的匹配度
	@Transient
	private int matchingRate;
	
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "users")
	private Set<Group> groups;
	

}
