package com.kos.CoCoCo.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserVO implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	@Id
	private String userId;
	
	private String pw;
	
	private String name;
	
	private String company;
	
	private String image;
	
	@CreationTimestamp
	private Timestamp regDate;
	
	private String status;

}
