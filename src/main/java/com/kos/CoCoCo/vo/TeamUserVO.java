package com.kos.CoCoCo.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "teamUser")
public class TeamUserVO implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	TeamUserMultikey teamUserId;
	
	@CreationTimestamp
	private Timestamp joinDate;
	
	private String userRole;
}
