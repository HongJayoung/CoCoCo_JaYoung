package com.kos.CoCoCo.sol.vo;

import java.sql.Timestamp;

import com.kos.CoCoCo.vo.NoticeVO;
import com.kos.CoCoCo.vo.TeamVO;
import com.kos.CoCoCo.vo.UserVO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class NoticeDTO {
	
	private Long noticeId;
	
	TeamVO team;
	
	private String noticeTitle;
	
	private String noticeText;
	
	UserVO user;
	
	private Timestamp noticeRegDate;
	
	private Timestamp noticeUpdate;
	
	private Integer fixedYN;
	
	
	public NoticeVO toEntity() {
		NoticeVO build = NoticeVO.builder()
				.noticeId(noticeId)
				.user(user)
				.team(team)
				.noticeTitle(noticeTitle)
				.noticeText(noticeText)
				.fixedYN(fixedYN)
				.build();
		return build;
	}
	
	@Builder
	public NoticeDTO(Long noticeId, TeamVO team, String noticeTitle, String noticeText, UserVO user, Timestamp noticeRegDate, Timestamp noticeUpdate, Integer fixedYN ) {
		this.noticeId = noticeId;
		this.team = team;
		this.noticeTitle = noticeTitle;
		this.noticeText = noticeText;
		this.user = user;
		this.noticeRegDate = noticeRegDate;
		this.noticeUpdate = noticeUpdate;
		this.fixedYN = fixedYN;
	}
}
