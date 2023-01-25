package com.kos.CoCoCo.ja0.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.chat.dto.ChatMessageDTO;

public interface MessageRepositoryH extends CrudRepository<ChatMessageDTO, Long>{
	
	List<ChatMessageDTO> findByWriter(String writer);

	@Query(value = "select * from message where writer = ?1", nativeQuery = true)
	List<ChatMessageDTO> findByUserId(String userId);
	
	@Modifying
	@Query(value = "delete from message where team_id = ?1", nativeQuery = true)
	void deleteByTeamId(Long teamId);
}
