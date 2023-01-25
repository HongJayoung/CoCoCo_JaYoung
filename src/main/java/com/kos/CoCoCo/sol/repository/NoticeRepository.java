package com.kos.CoCoCo.sol.repository;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.kos.CoCoCo.vo.NoticeVO;
import com.kos.CoCoCo.vo.QNoticeVO;
import com.kos.CoCoCo.vo.TeamVO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeVO, Long>,
	PagingAndSortingRepository<NoticeVO, Long>, QuerydslPredicateExecutor<NoticeVO> {
	
	public List<NoticeVO> findByTeamOrderByNoticeRegDateDesc(TeamVO team);
	
	public Page<NoticeVO> findByTeam(TeamVO team, Pageable pageable);

	public default Predicate makePredicate2(String type, String keyword, String keyword2, TeamVO team) {
		BooleanBuilder builder = (BooleanBuilder) makePredicate(type, keyword, keyword2);
		QNoticeVO notice = QNoticeVO.noticeVO;
		builder.and(notice.team.eq(team));
		return builder;
	}
	
   	public default Predicate makePredicate(String type, String keyword, String keyword2) {
		BooleanBuilder builder = new BooleanBuilder();
		QNoticeVO notice = QNoticeVO.noticeVO;
		
		builder.and(notice.noticeId.gt(0));
		System.out.println(type + keyword);
		//검색조건처리
		if(type==null) return builder;
		switch (type) {
		case "noticeTitleAndText" : 
			builder.and(notice.noticeText.like("%" + keyword + "%"));
			builder.or(notice.noticeTitle.like("%" + keyword + "%"));
			break;
		case "noticeTitle": builder.and(notice.noticeTitle.like("%" + keyword + "%")); break;
		case "noticeText": builder.and(notice.noticeText.like("%" + keyword + "%")); break;
		case "noticeRegDate": 
			Timestamp sdate, edate;
			sdate = convertDate(keyword);
			edate = convertDate(keyword2);
			builder.and(notice.noticeRegDate.between(sdate, edate)); break;
		default: break;
		}
		return builder;
		}
	
	public default Timestamp convertDate(String strDate) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date d;
		Timestamp ts = null;
		try {
			d = dateFormat.parse(strDate);
			ts = new Timestamp(d.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ts;
		
	}

	

	
}
