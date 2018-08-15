package com.shenghesun.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

//微信用户
@Entity
@Table
@Data
@ToString(exclude = {"choirs"})
@EqualsAndHashCode(exclude = {"choirs"},callSuper = true)
@JsonIgnoreProperties("choirs")
public class User extends BaseEntity implements Serializable,Comparable<User> {

	private static final long serialVersionUID = -7876850738135969925L;

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
	@Column
	private int age;
	
	
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "users")
	private Set<Choir> choirs;


	@Override
	public int compareTo(User o) {
        return this.id.compareTo(o.id);
	}


}
