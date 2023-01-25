package com.kos.CoCoCo.ja0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.ReplyVO;


public interface ReplyRepositoryH extends CrudRepository<ReplyVO, Long>{

	@Query(value = "select * from replies where user_user_id = ?1", nativeQuery = true)
	List<ReplyVO> findByUserId(String userId);
	
	@Query(value = "select * from replies where board_board_id = ?1", nativeQuery = true)
	List<ReplyVO> findByBoardId(Long boardId);
	
	@Modifying
	@Query(value="delete from replies where board_board_id=?1", nativeQuery = true)
	public void deleteByBoardId(Long boardId);
}
