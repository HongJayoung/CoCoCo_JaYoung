package com.kos.CoCoCo.ja0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.vo.BoardCategoryVO;


public interface BoardCategoryRepositoryH extends CrudRepository<BoardCategoryVO, Long>{
	
	@Query(value = "select * from board_category where team_team_id = ?1", nativeQuery = true)
	List<BoardCategoryVO> findByTeamId(Long teamId);
	
	
	@Query(value = "select * from board_category where board_category_id = ?1", nativeQuery = true)
	BoardCategoryVO findByCategoryId(Long categoryId);
	
	@Modifying
	@Query(value = "delete from board_category where board_category_id = ?1", nativeQuery = true)
	void deleteByCategoryId(Long categoryId);
	
	@Modifying
	@Query(value = "delete from board_category where team_team_id = ?1", nativeQuery = true)
	void deleteByTeamId(Long teamId);
}
