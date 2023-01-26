package com.kos.CoCoCo.ja0.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.ja0.VO.BoardFile;
import com.kos.CoCoCo.vo.BoardVO;

public interface BoardFileRepositoryH extends CrudRepository<BoardFile, Long> {

	public List<BoardFile> findByBoard(BoardVO board);

	@Modifying
	@Query(value="delete from board_file where board_board_id=?1", nativeQuery = true)
	public void deleteByBoardId(Long boardId);
}

