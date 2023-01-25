package com.kos.CoCoCo.vo;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "works")
public class WorkVO {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long workId;
	
	@ManyToOne
	TeamVO team;
	
	private String workTitle;
	
	private String workText;
	
	private Date workStart;
	
	private Date workEnd;
	
	private String workStatus;
	
	@Transient
	UserVO[] manager;

	@Transient
	String[] managerid;
}
