package com.kos.CoCoCo.vo;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.kos.CoCoCo.sol.vo.NoticeFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notice")
public class NoticeVO {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long noticeId;
	
	@ManyToOne
	TeamVO team;
	
	private String noticeTitle;
	
	private String noticeText;
	
	@ManyToOne
	UserVO user;
	
	@CreationTimestamp
	private Timestamp noticeRegDate;
	
	@UpdateTimestamp
	private Timestamp noticeUpdate;
	
	private Integer fixedYN;
	
	@OneToMany(mappedBy ="notice")
	private List<NoticeFile> file;
}


