package com.kos.CoCoCo.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.chat.dto.ChatMessageDTO;

public interface MessageRepository extends CrudRepository<ChatMessageDTO, Long>{
	
	
	@Query(value="select message.*, users.image writerImg from message join users on (message.writer = users.user_id) where team_Id = ?1 order by chat_id", nativeQuery = true)
	List<Object[]> findByTeamIdOrderById(Long teamId);

}
