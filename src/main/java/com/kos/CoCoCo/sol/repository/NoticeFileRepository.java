package com.kos.CoCoCo.sol.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.sol.vo.NoticeFile;
import com.kos.CoCoCo.vo.NoticeVO;

public interface NoticeFileRepository extends CrudRepository<NoticeFile, Long> {
	public List<NoticeFile> findByFileId(Long fileId);
		
	public List<NoticeFile> findByNotice(NoticeVO notice);
	
	public List<NoticeFile> deleteByNotice(NoticeVO notice);

	@Modifying
	@Query(value = "delete from notice_file where notice_notice_id = ?1", nativeQuery = true)
	void deleteByNoticeId(Long noticeId);


	

}

