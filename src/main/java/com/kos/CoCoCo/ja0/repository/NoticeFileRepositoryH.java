package com.kos.CoCoCo.ja0.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.sol.vo.NoticeFile;


public interface NoticeFileRepositoryH extends CrudRepository<NoticeFile, Long>{

	@Modifying
	@Query(value = "delete from notice_file where notice_notice_id = ?1", nativeQuery = true)
	void deleteByNoticeId(Long noticeId);
}
