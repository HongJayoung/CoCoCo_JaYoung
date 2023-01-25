package com.kos.CoCoCo.ja0.VO;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Entity
public class KakaoUser implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
    private String name;
    private String email;
    private String img;
    
}
