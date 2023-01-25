package com.kos.CoCoCo.sol.vo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.kos.CoCoCo.vo.NoticeVO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(exclude = "notice")
@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeFile {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long fileId;
	
	private String originFname;
	
	private String filename;
	
	
	@ManyToOne
	NoticeVO notice;
	
	 
	
}
