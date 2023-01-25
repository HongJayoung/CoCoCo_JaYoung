package com.kos.CoCoCo.ja0.VO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.kos.CoCoCo.vo.BoardVO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(exclude = "board")
@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardFile {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long fileId;
	
	private String originFname;
	
	private String filename;
	
	@ManyToOne
	BoardVO board;
}
