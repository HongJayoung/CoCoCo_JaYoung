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

import com.kos.CoCoCo.ja0.VO.BoardFile;
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
@Table(name = "boards")
public class BoardVO {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long boardId;
	
	private String boardTitle;
	
	private String boardText;
	
	@ManyToOne
	UserVO user;
	
	@CreationTimestamp
	private Timestamp boardRegDate;
	
	@UpdateTimestamp
	private Timestamp boardUpdate;
	
	@ManyToOne
	BoardCategoryVO category;
	
	@OneToMany(mappedBy ="board")
	private List<BoardFile> file;
}
