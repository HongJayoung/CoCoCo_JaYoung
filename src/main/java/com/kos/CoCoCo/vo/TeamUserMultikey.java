package com.kos.CoCoCo.vo;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TeamUserMultikey implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	TeamVO team;
	
	@ManyToOne
	UserVO user;
}
