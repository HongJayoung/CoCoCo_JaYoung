package com.kos.CoCoCo.ja0.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.ja0.VO.BoardFile;
import com.kos.CoCoCo.vo.BoardVO;

public interface BoardFileRepositoryH extends CrudRepository<BoardFile, Long> {

	public List<BoardFile> findByBoard(BoardVO board);

}

