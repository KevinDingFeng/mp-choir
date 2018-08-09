package com.shenghesun.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

//微信群
@Entity
@Table(name = "wx_group")
@Data
@ToString(exclude = {"users"})
@EqualsAndHashCode(exclude = {"users"})
@JsonIgnoreProperties("users")
public class Group implements Serializable{

	private static final long serialVersionUID = 5855327768914111196L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//微信群ID
	@Column
	private String openGid;
	
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "group_user_rel", 
	inverseJoinColumns = {@JoinColumn(name = "user_id")}, 
	joinColumns = {@JoinColumn(name = "group_id")})
	private Set<User> users;
	
	
}
