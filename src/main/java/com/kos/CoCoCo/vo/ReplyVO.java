package com.kos.CoCoCo.vo;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.kos.CoCoCo.ja0.VO.BoardFile;

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
public class ReplyVO {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long replyId;
	
	@ManyToOne
	BoardVO board;
	
	@ManyToOne
	UserVO user;
	
	private String replyText;
	
	@CreationTimestamp
	private Timestamp replyRegDate;
	
	@UpdateTimestamp
	private Timestamp replyUpdate;
}
