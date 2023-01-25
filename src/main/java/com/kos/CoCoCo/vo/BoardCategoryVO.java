package com.kos.CoCoCo.vo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boardCategory")
public class BoardCategoryVO {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long boardCategoryId;
	
	private String categoryName;
	
	@ManyToOne
	TeamVO team;

}
